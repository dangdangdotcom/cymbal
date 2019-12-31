#!/bin/bash

# Execute bgsave command for each instance in server.
# The instance already persistence with rdb or aof will be ignore.


# check if job is running
job_process=`ps -ef | grep "$0" | wc -l`
if [[ $job_process > 4 ]]; then
    echo "Another job is running now, this process will be stop."
    exit 1
fi

log() {
    echo "$(date "+%Y-%m-%d %H:%M:%S") * $1"
}
# globle config
default_sleep_seconds_wait_for_bgsave=30
job_intervel=600

# get redis dir
redis_dir=`ls ~/ | grep -E "redis-([0-9]{1,2}\.){2}[0-9]{1,2}$" | sed -n "1p"`

if [[ -z $redis_dir ]] ; then
    log "Please init server by redis-ops first."
    exit 1
fi
redis_client_dir="/home/redis/$redis_dir/src/redis-cli"

while [[ 1 = 1 ]]; do
    start_time=$(date "+%Y-%m-%d %H:%M:%S");
    log "Bgsave job is started at $start_time"

    # get redis server port
    redis_instance_dirs=`ls ~/ | grep -E "redis-[0-9]{4}$"`
    redis_instance_dirs_array=( $redis_instance_dirs )

    for dir in ${redis_instance_dirs_array[@]}
    do
        config_save=`cat /home/redis/$dir/redis.conf | grep -E "^save"`
        config_appendonly=`cat /home/redis/$dir/redis.conf | grep -E "^appendonly" | cut -d" " -f 2`

        # check if need persistence
        if [[ -z $config_save && $config_appendonly != "yes" ]]; then
            # check if need password
            config_password=`cat /home/redis/$dir/redis.conf | grep -E "^requirepass" | cut -d" " -f 2`

            require_pass=""
            if [[ -n $config_password ]]; then
                # 去除左右边双引号
                config_password=${config_password#\"}
                config_password=${config_password%\"}

                require_pass="-a $config_password"
            fi
            # get port
            config_port=`cat /home/redis/$dir/redis.conf | grep -E "^port" | cut -d" " -f 2 | sed -n "1p"`
            # check redis instance is running
            ping_result=`$redis_client_dir -p $config_port $require_pass ping`

            if [[ $ping_result = "PONG" ]]; then
                # call bgsave to target instance
                log "Start bgsave for port $config_port."
                bgsave_result=`$redis_client_dir -p $config_port $require_pass bgsave`

                # get last bgsave time to sleep
                rdb_bgsave_in_progress=1
                while [[ $rdb_bgsave_in_progress = 1 ]]; do
                    rdb_last_bgsave_time_sec=`$redis_client_dir -p $config_port $require_pass info | grep "rdb_last_bgsave_time_sec" | cut -d":" -f 2 | tr -d '\r'`
                    if [[ $rdb_last_bgsave_time_sec -le 0 ]]; then
                        # 30 is ge magic number
                        rdb_last_bgsave_time_sec=$default_sleep_seconds_wait_for_bgsave
                    fi

                    log "Sleep ${rdb_last_bgsave_time_sec}s to wait for bgsave."
                    sleep $rdb_last_bgsave_time_sec

                    rdb_bgsave_in_progress=`$redis_client_dir -p $config_port $require_pass info | grep "rdb_bgsave_in_progress" | cut -d":" -f 2`
                done

                log  "Bgsave is done of port $config_port."
            fi
        else
            log  "Redis server $dir is enable persistence by redis-server, ignore."
        fi
    done

    end_time=$(date "+%Y-%m-%d %H:%M:%S")
    duration=$(($(date +%s -d "${end_time}") - $(date +%s -d "${start_time}")))
    log "Bgsave job is done at $end_time, duration ${duration}s."

    if [[ $duration < $job_intervel ]]; then
        ((sec_to_sleep=$job_intervel-$duration))
        log "Sleep ${sec_to_sleep}s to wait for next job time."
        sleep $sec_to_sleep
    fi
done
