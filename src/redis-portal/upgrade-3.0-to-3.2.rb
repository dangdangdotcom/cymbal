#!/bin/env ruby

require 'socket'

def print_usage()
  print "
Upgrade Ruby 3.0 to 3.2.11
  Usage:
    #{__FILE__} <ip:port> <ip:port> ...

  For Example:
    #{__FILE__} 1.2.3.4:5001 1.2.3.4:5002 1.2.3.5:5001 1.2.3.5:5002 1.2.3.6:5001 1.2.3.6:5002
"
end

def abort(msg)
  print "[ERROR] #{msg}", "\n"
  exit 1
end

# get ip and port

$master_slave_pairs = {}

class ClusterNode
  def initialize(node, ip, port)
    @node = node
    @ip = ip
    @port = port
  end

  def get_ip
    @ip
  end

  def get_port
    @port
  end

  def upgrade_slave(master_ip, master_port)

    # 1. kill process and wait exit
    print "Stopping server: #{@ip}:#{@port}\n"
    print `ssh redis@#{@ip} 'ps -ef | grep redis-server | grep #{@port} | grep -v grep | awk "{print \\$2}" | xargs kill' `, "\n"

    while true
      print "Waiting for redis-server exit...  server: #{@ip}:#{@port}\n"
      sleep 3

      res = `ssh redis@#{@ip} 'ps -ef | grep redis-server | grep #{@port} | grep -v grep | awk "{print \\$2}"'`.chop
      if res == ""
        print "Redis server have been stopped. server: #{@ip}:#{@port}\n"
        break
      end
    end

    # 1a. add new config for 3.2
    print `ssh redis@#{@ip} "cd redis-#{@port} && grep -E 'protected-mode (yes|no)' redis.conf > /dev/null || echo 'protected-mode no' >> redis.conf"`, "\n"

    # 1b. Rename rdb file and aof file to prevent load data on restart server.
    # (a slave server will sync data from master. so no need to reload data from disk)
    print `ssh redis@#{@ip} "cd redis-#{@port}; mkdir -p upgrade-temp; mv *.aof *.rdb upgrade-temp"`, "\n"

    # 2. start process
    print "Starting redis server.  server: #{@ip}:#{@port}\n"
    print `ssh redis@#{@ip} "cd ~/redis-#{@port}; ~/redis-3.2.11/src/redis-server redis.conf"`, "\n"
    if $?.to_i != 0
      abort "Start redis server failed! server: #{@ip}:#{@port}"
    end

    sleep 1

    # 3. waiting for replication almost done
    wait_replication_almost_done(master_ip, master_port, @ip, @port)
  end

  def failover(master_ip, master_port)
    password = get_password(@ip, @port)
    sock = TCPSocket.open(@ip, @port, password)

    sock.puts "cluster failover"
    res = sock.gets
    res = res.chop
    if res != "+OK"
      sock.close
      abort "Do failover failed. The server #{slave.get_ip}:#{slave.get_port} returns error message: #{res}"
    end

    print "==== Do failover successful. ====\n\n"

  end

  def to_s
    "#{@node} #{@ip}:#{@port}"
  end
end

def get_password(ip, port)
  `ssh redis@#{ip} "grep -E '^requirepass' ~/redis-#{port}/redis.conf | awk '{ print \\$2 }'"`.chop
end

def connect_and_auth(ip, port, password)
  sock = TCPSocket.open(ip, port)

  if password != ""
    sock.puts "auth #{password}"
    res = sock.gets
    res = res.chop
    if res != "+OK"
      sock.close
      abort "Authenticate failed. The server #{slave_ip}:#{slave_port} returns error message: #{res}"
    end
  end
  sock
end

def wait_replication_almost_done(master_ip, master_port, slave_ip, slave_port)
  password = get_password(master_ip, master_port)
  sock = connect_and_auth(master_ip, master_port, password)

  print "Waiting for replication complete...\n"
  while true
    sleep 1

    sock.puts "info replication"
    master_offset = nil
    offset = nil

    while (line = sock.gets)
      if line == nil
        print "EOF\n"
        break
      end

      if line.chop == ""
        break
      end

      if line =~ /^-/
        abort "Command 'info replication' returns error message #{line}"
      end

      m = /slave\d+:ip=([^,]+),port=([^,]+),.+,offset=(\d+)/.match(line)
      if m != nil && m[1] == slave_ip && m[2] == slave_port
        offset = m[3].to_i
      elsif (m = /master_repl_offset:(\d+)/.match(line)) != nil
        master_offset = m[1].to_i
      end
    end

    print "\rmaster-offset: #{master_offset}, slave-offset: #{offset}        "
    if offset != nil && offset > 0 && master_offset - offset < 1024 * 1024
      print "\nReplication almost done.\n"
      return
    end
  end
end

class MasterSlavePair
  def initialize()
    @slaves = []
  end

  def set_master(node, addr)
    ip, port = addr.split(/:/)
    @master = ClusterNode.new(node, ip, port)
  end

  def add_slave(node, addr)
    ip, port = addr.split(/:/)
    @slaves.push(ClusterNode.new(node, ip, port))
  end

  def upgrade()
    # 1. upgrade all slaves
    @slaves.each { |node|
      node.upgrade_slave(@master.get_ip, @master.get_port)
    }

    # do fail-over at one slave
    slave = @slaves[0]
    slave.failover(@master.get_ip, @master.get_port)

    sleep 5

    # on new slave (was master)
    @master.upgrade_slave(slave.get_ip, slave.get_port)
  end

  def to_s()
    "#{@master} <= #{@slaves.join('\n                                                  ')}"
  end

end

def get_cluster_topology(addrs)
  infos = addrs.map { |x|
    ip, port = x.split(/:/)
    sock = TCPSocket.open(ip, port)
    sock.puts "cluster nodes"
    items = []

    while (line = sock.gets)
      line = line.chop
      if line == ""
        break
      end

      if line =~ /^-/
        abort "Get cluster nodes failed with message #{line}"
      end

      if line =~ /^[^$]/
        line = line.gsub(/myself,/, '')
        items.push line.split(/\s+/)[0, 4].join(' ')
      end
    end

    sock.close
    items.sort.join("\n")
  }

  if infos.uniq.length > 1
    abort "cluster information not consistence!"
  end

  infos[0].split(/\n/).each { |x|
    node_id, addr, type, ref = x.split(/\s+/)

    if type == "slave"
      item = $master_slave_pairs[ref]
      if item == nil
        $master_slave_pairs[ref] = MasterSlavePair.new
        $master_slave_pairs[ref].add_slave(node_id, addr)
      else
        item.add_slave(node_id, addr)
      end
    elsif type == "master"
      item = $master_slave_pairs[node_id]
      if item == nil
        $master_slave_pairs[node_id] = MasterSlavePair.new
        $master_slave_pairs[node_id].set_master(node_id, addr)
      else
        item.set_master(node_id, addr)
      end
    end
  }

  $master_slave_pairs.each { |k, x|
    print x, "\n"
  }

end

# install redis-3.2.11
# 1. check
# 2. copy package file
# 3. build source code
def install_redis(ips)
  print "==== Begin install redis on follow server(s). " + ips.join(', ') + " ====\n"
  ips.each { |ip|
    have_redis_32 = `ssh redis@#{ip} "stat redis-3.2.11"`
    if have_redis_32 == ""
      # install redis
      print "Install redis-3.2.11 ...\n"
      system "scp ~/redis-3.2.11.tar.gz redis@#{ip}:."
      if $?.to_i != 0
        abort "Copy redis-3.2.11.tar.gz to remote host failed!"
      end

      system "ssh redis@#{ip} 'tar -zxf redis-3.2.11.tar.gz && cd redis-3.2.11 && make'"
      if $?.to_i != 0
        abort "Build redis-3.2.11 failed!"
      end

      print "Redis install complete!\n"
    else
      print "Redis-3.2.11 already installed!\n"
    end
  }

  print "\n==== All install completed! ====\n\n"
end

IPs = ARGV.map { |x|
  x.split(/:/)[0]
}.uniq

if IPs.length == 0
  print_usage
  exit 1
end

install_redis IPs
get_cluster_topology ARGV

$master_slave_pairs.each { |k, item|
  print "\n==== Begin upgrade a sharding for ====\n"
  print item, "\n"
  item.upgrade
}

print "\n==== All upgrade completed ====\n"
print "================================\n"
