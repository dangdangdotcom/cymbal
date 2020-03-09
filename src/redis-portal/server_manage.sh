#!/bin/sh
# -----------------------------------------------------------------------------
# Manage Script for the Redis Server of dd-ops-platform
#
# The directory structure of redis server and management server is like this:
#
#   Management Server
#     base_path：/home/redis/redis-portal/
#     The structure of scrips：
#       {$base_path}/
#       |-- {other_script.sh}
#       |-- monitor.sh
#       |-- redis_scp_auth.sh
#       |-- server_manage.sh
#       |-- sentinel_manage.sh
#       `-- start_monitor.sh
#     The structure of conf：
#       {$base_path}/conf/redis-{version}/
#       |-- {clusterid}
#       |   |-- {IP}:{PORT}
#       |   |   |-- redis-user.conf
#       |   |   `-- redis.conf
#       |-- {redis-cluster-common.conf}
#       |-- {redis-single-common.conf}
#       |-- {sentinel.conf}
#       `-- redis-user.conf
#
#   Redis Server
#     base_path：/home/redis/
#     The structure of redis：
#       {$base_path}/redis-{version}/
#       |-- redis-server
#       |-- redis-cli
#       |-- redis-sentinel
#       |-- {redis-trib.rb}
#       |-- redis-{PORT}
#       |   |-- sentinel.conf
#       |   `-- sentinel.log
#       |-- redis-{PORT}
#       |   |-- redis.conf
#       |   |-- redis-user.conf
#       |   |-- redis-user.conf.bak
#       |   |-- dump.rdb
#       |   |-- appendonly.aof
#       |   `-- redis.log
#       `-- redis-{PORT}
#           |-- redis.conf
#           |-- redis-user.conf
#           |-- redis-user.conf.bak
#           |-- nodes.conf
#           |-- dump.rdb
#           |-- appendonly.aof
#           `-- redis.log
#
# -----------------------------------------------------------------------------

show_usage()
{
    echo "Usage: server_manage.sh version command password agrs..."
    echo "version:"
    echo "  redis-2.8.24   The single mode of redis version."
    echo "  redis-3.0.6    The cluster mode of redis version"
    echo "  redis-3.2.11   The cluster mode of redis version"
    echo "command:"
    echo "  create_cluster            In cluster mode, use redis-cli --cluster create command"
    echo "                            to create cluster, the args format is:"
    echo "                            num ip1:port1 ip2:port2 ip3:port3 ..."
    echo "  apply                     To init something of redis server, such like" 
    echo "                            create folder, copy config file etc. The args format is:" 
    echo "                            ip port clusterid mode(single|cluster) cachesize(unit is gb)"
    echo "  start                     Start redis server. The args format is:" 
    echo "                            ip port."
    echo "  stop                      Stop redis server.  The args format is:" 
    echo "                            ip port."
    echo "  monitor                   Monitor redis server.  The args format is:" 
    echo "                            ip port."
    echo "  check                     Check role of the specified redis instance. The args format is:"
    echo "                            ip port"
    echo "  config                    Config redis conf. The args format is:"
    echo "                            ip port clusterid itemname 'itemvalue'. Such like:"
    echo "                            127.0.0.1 6379 DyLJde4H timeout '20',"
    echo "                            127.0.0.1 6379 DyLJde4H save '900 1 300 10 60 10000'."
    echo "  slaveof                   Only for single mode,  The args format is:"
    echo "                            clusterid slaveip slaveport masterip masterport masterpassword."
    echo "  cluster_nodes             Get cluster nodes info. The args format is:"
    echo "                            ip port."
    echo "  cluster_meet              Add node to the cluster, make it one of the cluster member. The args format is:"
    echo "                            newip newport existingip existingport."
    echo "  cluster_replicate         Make the specified node slaveof the target node. The args format is:"
    echo "                            slaveip slaveport masterip masterport."
    echo "  del_cluster_node          Removing node from cluster. First let cluster forget the node, then shutdown the node,"
    echo "                            and last remove the node info including redis.conf log etc.The args format is:"
    echo "                            ip port clusterid."
    echo "  cluster_failover          Toggle current node 's role from slave to master, and origin master to slave."
    echo "                            The args format is: ip port"
    echo "  redis_cli_cmd             Send cmd to redis node with redis-cli -c. The args format is: ip port rediscommand"
    echo "  import                    To import a existing redis server. The process is base on command apply,"
    echo "                            and include some another operation, such as: create hard link to .aof,"
    echo "                            .rdb and nodes.conf files, and so on."
    echo "                            The args format is: ip port clusterid mode(single|cluster) cachesize(unit is gb)."
    echo "Note: Above, ip and port is referred to destination server, clusterid is generated by application, not by redis."
}

# Set connection timeout to 3 seconds
alias ssh='ssh -o ConnectTimeout=3'
alias scp='scp -o ConnectTimeout=3'

if [ -z $1 ] || [ -z $2 ] ; then
    show_usage
    exit 1
fi

shell_path=$0
redis_version=$1
command=$2
password=$3
require_pass=""
if [ ! -z $password ]; then
    require_pass="-a '$password'"
fi

local_base_path=$(cd "$(dirname "$shell_path")" >/dev/null; pwd)
local_base_conf_path=$(cd "$local_base_path/conf/$redis_version" >/dev/null; pwd)

# Shift 'version' , 'command' and 'password' argument
shift 3

apply_systemd_service_for_centos_7() {
    # systemd init
    # check if support systemd
    systemctl_path=$(ssh redis@$ip "whereis systemctl | cut -d ':' -f 2")

    # irq_affinity
    numa_nodes=$(ssh redis@$ip "lscpu | grep -E 'NUMA node[[:digit:]]'" |  tr -s  ' ' |  cut -d ':' -f 2,4)
    numa_nodes=(${numa_nodes//\n/ })
    numa_node_size=${#numa_nodes[@]}

    # only one numa node, use it for redis-server process
    if [[ $numa_node_size = 1 ]]; then
        taskset_cpus=${numa_nodes[0]}
    else
        # multi numa nodes, use 1 to last for redis-server process
        for (( i=1; i<${#numa_nodes[@]}; i=i+1 ));
        do
            numa_node_cpus=${numa_nodes[$i]}
            if [[ "$numa_node_cpus" =~ "-" ]] ; then
                cpu_side=(${numa_node_cpus//-/ })
                for cpu_index in $(seq ${cpu_side[0]} ${cpu_side[1]})
                do
                    taskset_cpus="$taskset_cpus,$cpu_index"
                done
            elif [[ "$numa_node_cpus" =~ "," ]] ; then
                taskset_cpus="$taskset_cpus,$numa_node_cpus"
            else
                taskset_cpus="$taskset_cpus,$numa_node_cpus"
            fi
        done

        if [[ "$taskset_cpus" == ","* ]]; then
            taskset_cpus=${taskset_cpus:1}
        fi
    fi

    if [[ -n $systemctl_path ]]; then
        scp $local_base_path/systemd.service/redis.service root@$ip:/etc/systemd/system/redis-$port.service
        ssh root@$ip "sed -i -e 's/\${redis_port}/$port/g'\
            -e 's/\${redis_version}/$redis_version/g'\
            -e 's/\${redis_password}/$password/g'\
            /etc/systemd/system/redis-$port.service\
            && systemctl daemon-reload\
            && systemctl enable redis-$port >/dev/null 2>&1"
    fi
}

if [ "$command" = "create_cluster" ]; then
    server_num=$1; shift 1
    ips=$*
    echo 'yes' | redis-cli --cluster create --cluster-replicas $server_num $ips
elif [ "$command" = "apply" ]; then
    ip=$1
    port=$2
    cluster_id=$3
    cluster_mode=$4
    maxmemory=$5
    cluster_conf_path=$local_base_conf_path/$cluster_id/$ip:$port
    mkdir -p $cluster_conf_path
    cp $local_base_conf_path/redis-$cluster_mode-common.conf $cluster_conf_path/redis.conf;

    if [ ! -z $password ]; then
        echo "requirepass $password" >> $cluster_conf_path/redis.conf
        echo "masterauth $password" >> $cluster_conf_path/redis.conf
    fi
    echo "port $port" >> $cluster_conf_path/redis.conf
    echo "maxmemory $maxmemory" >> $cluster_conf_path/redis.conf

    ssh redis@$ip "mkdir -p ~/redis-$port"
    scp $cluster_conf_path/redis.conf redis@$ip:~/redis-$port

    apply_systemd_service_for_centos_7
elif [ "$command" = "start" ]; then
    ip=$1
    port=$2

    # systemd init
    # check if support systemd
    systemctl_path=$(ssh redis@$ip "whereis systemctl | cut -d ':' -f 2")
    if [[ -n $systemctl_path ]]; then
        redis_service_state=$(ssh redis@$ip "systemctl show redis-$port --property LoadState")
        if [[ $redis_service_state == "LoadState=not-found" ]]; then
            apply_systemd_service_for_centos_7
        fi
        ssh root@$ip "systemctl start redis-$port"
    else
        ssh redis@$ip "cd ~/redis-$port;~/$redis_version/src/redis-server redis.conf >/dev/null 2>&1 &"
    fi
elif [ "$command" = "stop" ]; then
    ip=$1
    port=$2

    # systemd init
    # check if support systemd
    systemctl_path=$(ssh redis@$ip "whereis systemctl | cut -d ':' -f 2")
    if [[ -n $systemctl_path ]]; then
        redis_service_state=$(ssh redis@$ip "systemctl show redis-$port --property LoadState")
        if [[ $redis_service_state == "LoadState=loaded" ]]; then
            ssh root@$ip "systemctl stop redis-$port"
        else
            ssh redis@$ip "nohup ~/$redis_version/src/redis-cli -h $ip -p $port $require_pass shutdown"
        fi
    else
        ssh redis@$ip "nohup ~/$redis_version/src/redis-cli -h $ip -p $port $require_pass shutdown"
    fi
elif [ "$command" = "monitor" ]; then
    ip=$1
    port=$2
    ssh redis@$ip "~/$redis_version/src/redis-cli  -h $ip -p $port $require_pass info | grep -E 'keyspace|used_memory|connected_clients|output'"
elif [ "$command" = "check" ]; then
    ip=$1
    port=$2
    ssh redis@$ip "~/$redis_version/src/redis-cli -h $ip -p $port $require_pass info | grep -E 'role:|master_host:|master_port:' | cut -d ':' -f 2"
elif [ "$command" = "config" ]; then
    ip=$1
    port=$2
    cluster_id=$3
    item_name=$4
    item_value=$5
    cluster_conf_path=$local_base_conf_path/$cluster_id/$ip:$port
    
    ssh redis@$ip "~/$redis_version/src/redis-cli -h $ip -p $port $require_pass CONFIG SET $item_name '$item_value'"
    ssh redis@$ip "cd ~/redis-$port; cat redis.conf > redis.conf.bak; grep -v '^$item_name' redis.conf.bak > redis.conf"

    # check if there a # Generated by OPS PLATFORM in redis.conf
    need_print_annotation=$(ssh redis@$ip "grep '# Generated by OPS PLATFORM' ~/redis-$port/redis.conf")
    if [ -z "$need_print_annotation" ]; then
        ssh redis@$ip "cd ~/redis-$port; echo -e '\n# Generated by OPS PLATFORM' >> redis.conf"
    fi

    if [ "$item_name" = "save" ]; then
        array=( $item_value )
        for (( i=0; i<${#array[@]}; i=i+2 ));
        do
            ssh redis@$ip "cd ~/redis-$port; echo $item_name ${array[$i]} ${array[$i+1]} >> redis.conf"
        done
    else
        ssh redis@$ip "cd ~/redis-$port; echo $item_name $item_value >> redis.conf"
    fi

    scp redis@$ip:~/redis-$port/redis.conf $cluster_conf_path/redis.conf
elif [ "$command" = "slaveof" ]; then
    cluster_id=$1
    slave_server_ip=$2
    slave_server_port=$3
    master_server_ip=$4
    master_server_port=$5
    master_password=$6
    cluster_conf_path=$local_base_conf_path/$cluster_id/$slave_server_ip:$slave_server_port
    
    ssh redis@$slave_server_ip "~/$redis_version/src/redis-cli -h $slave_server_ip -p $slave_server_port $require_pass CONFIG SET masterauth '$master_password'"
    ssh redis@$slave_server_ip "~/$redis_version/src/redis-cli -h $slave_server_ip -p $slave_server_port $require_pass SLAVEOF $master_server_ip $master_server_port"
    ssh redis@$slave_server_ip "cd ~/redis-$slave_server_port; cat redis.conf > redis.conf.bak; grep -v '^slaveof\|masterauth' redis.conf.bak > redis.conf"
    if [ -n "$master_password" ]; then
        ssh redis@$slave_server_ip "cd ~/redis-$slave_server_port; echo masterauth $master_password >> redis.conf"
    fi
    if [ "$master_server_ip" != "NO" ]; then
        ssh redis@$slave_server_ip "cd ~/redis-$slave_server_port; echo slaveof $master_server_ip $master_server_port >> redis.conf"
    fi
    scp redis@$slave_server_ip:~/redis-$slave_server_port/redis.conf $cluster_conf_path/redis.conf
elif [ "$command" = "cluster_nodes" ]; then
    ip=$1
    port=$2
    
    ssh redis@$ip "~/$redis_version/src/redis-cli -h $ip -p $port $require_pass cluster nodes"
elif [ "$command" = "cluster_meet" ]; then
    new_ip=$1
    new_port=$2
    existing_ip=$3
    existing_port=$4
    
    ssh redis@$existing_ip "~/$redis_version/src/redis-cli -h $existing_ip -p $existing_port $require_pass cluster meet $new_ip $new_port"
elif [ "$command" = "cluster_replicate" ]; then
    slave_ip=$1
    slave_port=$2
    master_ip=$3
    master_port=$4
    
    node_id="$(redis-cli --cluster check $master_ip:$master_port | grep $master_ip:$master_port | tail -1 | cut -d ' ' -f 2)"
    if [ -n "$node_id" ]; then
        ssh redis@$slave_ip "~/$redis_version/src/redis-cli -h $slave_ip -p $slave_port $require_pass cluster replicate $node_id"
    fi
elif [ "$command" = "del_cluster_node" ]; then
    ip=$1
    port=$2
    cluster_id=$3
    cluster_conf_path=$local_base_conf_path/$cluster_id/$ip:$port

    node_id="$(redis-cli --cluster check $ip:$port | grep $ip:$port | tail -1 | cut -d ' ' -f 2)"
    if [ -n "$node_id" ]; then
        redis-cli --cluster del-node $ip:$port $node_id
        ssh redis@$ip "rm -fr ~/redis-$port"
        rm -fr $cluster_conf_path
    fi
elif [ "$command" = "cluster_failover" ]; then
    ip=$1
    port=$2

    ssh redis@$ip "~/$redis_version/src/redis-cli -h $ip -p $port $require_pass cluster failover"
elif [ "$command" = "redis_cli_cmd" ]; then
    ip=$1
    port=$2
    rediscommand=$3
    # no-raw or raw
    format=$4

    ssh redis@$ip "~/$redis_version/src/redis-cli --$format -h $ip -p $port $require_pass -c $rediscommand"
elif [ "$command" = "import" ]; then
    ip=$1
    port=$2
    cluster_id=$3
    cluster_mode=$4
    maxmemory=$5
    slave_of=$6
    cluster_conf_path=$local_base_conf_path/$cluster_id/$ip:$port
    mkdir -p $cluster_conf_path
    cp $local_base_conf_path/redis-$cluster_mode-common.conf $cluster_conf_path/redis.conf;

    if [ ! -z $password ]; then
        echo "requirepass $password" >> $cluster_conf_path/redis.conf
        echo "masterauth $password" >> $cluster_conf_path/redis.conf
    fi
    echo "port $port" >> $cluster_conf_path/redis.conf
    echo "maxmemory $maxmemory" >> $cluster_conf_path/redis.conf

    if [ ! -z "$slave_of" ]; then
        echo "slaveof $slave_of" >> $cluster_conf_path/redis.conf
    fi

    ssh redis@$ip "mkdir -p ~/redis-$port"
    scp $cluster_conf_path/redis.conf redis@$ip:~/redis-$port

    # hard link to some import file
    # get config
    configFilePath=$(ssh redis@$ip "~/$redis_version/src/redis-cli -h $ip -p $port $require_pass info | awk -F ':' '/config_file/{printf \$2}' | tr -d '\r'")

    # info 中取不到.config文件信息，说明是旧版本redis，只建立基础文件
    if [ -z $configFilePath ]; then
        # make true when import cluster, the owner of log file is user redis
        ssh redis@$ip "touch ~/redis-$port/redis.log"
    else
        configValues=$(ssh root@$ip "cat $configFilePath | grep -w -E '^[[:space:]]*dir|^[[:space:]]*logfile|^[[:space:]]*dbfilename|^[[:space:]]*appendfilename|^[[:space:]]*cluster-config-file'")
        array=( $configValues )
        declare -A configMap=()
        for (( i=0; i<${#array[@]}; i=i+2 ));
        do
            value=${array[$i+1]}

            # 去除左右边双引号
            value=${value#\"}
            value=${value%\"}

            configMap[${array[$i]}]=$value
        done

        dir=${configMap["dir"]}
        baseDir=${configFilePath%/*}

        if [[ $dir == "./" ]]; then
            dir="$baseDir"
        elif [[ $dir != /* ]]; then
             dir="$baseDir/$dir"
        fi

        log=${configMap["logfile"]}
        if [ -z $log ]; then
            log="redis.log"
        fi
        # 如果log file不存在，则按默认名称生成一个
        ssh root@$ip "if [ -e $dir/$log ]; then
                      ln $dir/$log /home/redis/redis-$port/redis.log
                   else
                      touch /home/redis/redis-$port/redis.log
                   fi
                   chown redis.redis /home/redis/redis-$port/redis.log"

        dbFileName=${configMap["dbfilename"]}
        if [ -z $dbFileName ]; then
            dbFileName="dump.rdb"
        fi

        appendFileName=${configMap["appendfilename"]}
        if [ -z $appendFileName ]; then
            appendFileName="appendonly.aof"
        fi

        scp /home/redis/redis-portal/sync_persistence_file.py root@$ip:/tmp/
        ssh root@$ip "nohup python /tmp/sync_persistence_file.py '$dir/$dbFileName' '/home/redis/redis-$port/dump.rdb' '$dir/$appendFileName' '/home/redis/redis-$port/appendonly.aof' > /dev/null 2>&1 &"

        if [ "$cluster_mode" = "cluster" ]; then
            clusterConfFile=${configMap["cluster-config-file"]}
            if [ -z $clusterConfFile ]; then
                clusterConfFile="$dir/nodes.conf"
            elif [[ $clusterConfFile != /* ]]; then
                clusterConfFile="$dir/$clusterConfFile"
            fi

            ssh root@$ip "ls $clusterConfFile && cp $clusterConfFile /home/redis/redis-$port/nodes.conf && chown redis.redis /home/redis/redis-$port/nodes.conf && chmod a+rw /home/redis/redis-$port/nodes.conf"
        fi
    fi
else
    show_usage
    exit 1
fi
