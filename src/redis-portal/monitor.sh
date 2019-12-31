redis-cli -h 10.255.209.182 -p 7001 info | grep -E "keyspace|total"
redis-cli -h 10.255.209.182 -p 8381 info | grep -E "keyspace|total"