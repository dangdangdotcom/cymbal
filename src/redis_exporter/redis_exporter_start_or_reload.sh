#!/bin/bash

# kill旧进程
./redis_exporter_shutdown.sh

# 拼装启动参数
redis_addr=""
redis_alias=""
redis_password=""

for each in `ls ./conf.d/`
    do
        redis_server="./conf.d/"$each
        if [ -f $redis_server ]; then
            redis_addr=${redis_addr}$each","

            # get clusterId and password
            # format: clusterId,password
            clusterIdAndPassword=`cat $redis_server`

            # split
            OLD_IFS="$IFS"
            IFS=","
            array=( $clusterIdAndPassword )
            IFS="$OLD_IFS"

            clusterId=""
            password=""

            if [ ${#array[@]} -ge 1 ]; then
                clusterId=${array[0]}
            fi
            if [ ${#array[@]} -ge 2 ]; then
                password=${array[1]}
            fi

            redis_alias=${redis_alias}$clusterId","
            redis_password=${redis_password}$password","
        fi
    done

redis_addr=${redis_addr%,*}
redis_alias=${redis_alias%,*}
redis_password=${redis_password%,*}

# 检查配置文件是否正确
if [ -z $redis_addr ] || [ -z $redis_alias ]; then
    echo "start redis_exporter fail: check configs in conf.d" 1>&2
    exit 1
fi

# 启动新进程
nohup ./redis_exporter -redis.addr "${redis_addr}" -redis.password "${redis_password}" -redis.alias "${redis_alias}" -redis-only-metrics > redis_exporter.log 2>&1 &
echo ok