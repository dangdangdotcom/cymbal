#!/bin/bash

mkdir software_package;
wget https://github.com/antirez/redis/archive/2.8.24.tar.gz -O ./software_package/redis-2.8.24.tar.gz
wget https://github.com/antirez/redis/archive/3.0.6.tar.gz -O ./software_package/redis-3.0.6.tar.gz
wget https://github.com/antirez/redis/archive/3.2.11.tar.gz -O ./software_package/redis-3.2.11.tar.gz
wget https://github.com/antirez/redis/archive/4.0.12.tar.gz -O ./software_package/redis-4.0.12.tar.gz
wget https://github.com/antirez/redis/archive/5.0.3.tar.gz -O ./software_package/redis-5.0.3.tar.gz