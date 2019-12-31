#!/bin/bash

# 获取redis_exporter进程号
pid=`ps -ef|grep -i './redis_exporter -redis.addr'|grep -vE "(grep|$$)"|awk '{print $2}'`

# 杀死旧进程
if [ ! -z ${pid} ]; then
    echo "killing running redis_exporter"
    kill -9 $pid
fi