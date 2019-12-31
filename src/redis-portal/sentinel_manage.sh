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

# Set connection timeout to 3 seconds
alias ssh='ssh -o ConnectTimeout=3'
alias scp='scp -o ConnectTimeout=3'

show_usage()
{
    echo "Usage: server_manage.sh version name command password agrs..."
    echo "version:"
    echo "  redis-2.8.24              The single mode of redis version."
    echo "name:"
    echo "  mymaster-<cluster_id>     The master name which to monitor."
    echo "command:"
    echo "  apply                     To init something of redis sentinel, such like" 
    echo "                            create folder, copy config file etc. The args format is:" 
    echo "                            ip port clusterid masterip masterport quorum."
    echo "  start                     Start redis sentinel. The args format is:" 
    echo "                            ip port."
    echo "  stop                      Stop redis server.  The args format is:" 
    echo "                            ip port."
    echo "  check                     Check role of the specified redis sentinel. The args format is:"
    echo "                            ip port"
    echo "  config                    Config sentinel conf. The args format is:"
    echo "                            ip port clusterid itemname 'itemvalue'. Such like:"
    echo "                            127.0.0.1 26379 DyLJde4H down-after-milliseconds '60000'."
    echo "Note: Above, ip and port is referred to destination sentinel instance."
}

if [ -z $1 ] || [ -z $2 ] ; then
    show_usage
    exit 1
fi

shell_path=$0
redis_version=$1
master_name=$2
command=$3
password=$4
local_base_path=$(cd "$(dirname "$shell_path")" >/dev/null; pwd)
local_base_conf_path=$(cd "$local_base_path/conf/$redis_version" >/dev/null; pwd)
remote_base_path="~/$redis_version/src"

#shift 'version', 'name' , 'command' and 'password' argument
shift 4

if [ "$command" = "apply" ]; then
    ip=$1
    port=$2
    cluster_id=$3
    master_ip=$4
    master_port=$5
    quorum=$6
    cluster_conf_path=$local_base_conf_path/$cluster_id/$ip:$port
    remote_inst_path=~/redis-$port
    mkdir -p $cluster_conf_path
    cp $local_base_conf_path/sentinel.conf $cluster_conf_path/sentinel.conf;
    echo "port $port" >> $cluster_conf_path/sentinel.conf;
    echo "dir $remote_inst_path" >> $cluster_conf_path/sentinel.conf;
    echo "sentinel monitor $master_name $master_ip $master_port $quorum" >> $cluster_conf_path/sentinel.conf;
    if [ ! -z $password ]; then
        echo "sentinel auth-pass $master_name $password" >> $cluster_conf_path/sentinel.conf
    fi
    echo "sentinel down-after-milliseconds $master_name 30000" >> $cluster_conf_path/sentinel.conf;
    echo "sentinel parallel-syncs $master_name 1" >> $cluster_conf_path/sentinel.conf;
    echo "sentinel failover-timeout $master_name 180000" >> $cluster_conf_path/sentinel.conf;
    ssh redis@$ip "mkdir -p $remote_inst_path"
    scp $cluster_conf_path/sentinel.conf redis@$ip:$remote_inst_path;
elif [ "$command" = "start" ]; then
    ip=$1
    port=$2
    remote_inst_path=~/redis-$port
    ssh redis@$ip "cd $remote_inst_path;nohup $remote_base_path/redis-sentinel sentinel.conf &> $remote_inst_path/sentinel.log &"
elif [ "$command" = "stop" ]; then
    ip=$1
    port=$2
    ssh redis@$ip "nohup $remote_base_path/redis-cli -h $ip -p $port -a '$password' shutdown"
elif [ "$command" = "check" ]; then
    ip=$1
    port=$2
    ssh redis@$ip "$remote_base_path/redis-cli -h $ip -p $port -a '$password' ping"
elif [ "$command" = "config" ]; then
    ip=$1
    port=$2
    cluster_id=$3
    item_name=$4
    item_value=$5
    cluster_conf_path=$local_base_conf_path/$cluster_id/$ip:$port
    remote_inst_path=~/redis-$port
    
    ssh redis@$ip "$remote_base_path/redis-cli -h $ip -p $port -a '$password' SENTINEL SET $master_name $item_name '$item_value'"
    scp redis@$ip:$remote_inst_path/sentinel.conf $cluster_conf_path/sentinel.conf
else
    show_usage
    exit 1
fi
