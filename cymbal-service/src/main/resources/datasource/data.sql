USE cymbal;

INSERT INTO `user_role` (`id`, `user_en_name`, `role_id`, creation_date, last_changed_date) VALUES(1, 'gezhen', 1, NOW(), NOW());

INSERT INTO `role_dict` (`id`, `role_name`, creation_date, last_changed_date) VALUES(1, 'system_administrator', NOW(), NOW());

insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','appendonly','yes','yes, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','appendfsync','no','everysec, always, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','maxmemory-policy','volatile-lru','volatile-lru, allkeys-lru, volatile-random, allkeys-random, volatile-ttl, noeviction');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','maxclients','10000','1-65000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','slowlog-log-slower-than','1000','0-60000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','slowlog-max-len','128','0-1000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','tcp-keepalive','0','0-2147483647');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','timeout','0','0,20-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-2.8.24','save','\"900 1 300 10 60 10000\"','for example:\"900 1 300 10 60 10000\"');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-sentinel','down-after-milliseconds','30000','5000-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-sentinel','failover-timeout','180000','60000-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('standalone','redis-sentinel','parallel-syncs','1','1-');

insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','appendonly','yes','yes, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','appendfsync','everysec','everysec,always,no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','maxmemory-policy','noeviction','volatile-lru, allkeys-lru, volatile-random, allkeys-random, volatile');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','maxclients','10000','1-65000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','slowlog-log-slower-than','1000','0-60000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','slowlog-max-len','128','0-1000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','tcp-keepalive','0','0-2147483647');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','timeout','0','0,10-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','cluster-node-timeout','10000','1000-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','cluster-slave-validity-factor','10','0,1-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.0.6','save','\"900 1 300 10 60 10000\"','for example:\"900 1 300 10 60 10000\"');

insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','appendonly','yes','yes, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','appendfsync','everysec','everysec, always, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','maxmemory-policy','noeviction','volatile-lru, allkeys-lru, volatile-random, allkeys-random, volatile');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','maxclients','10000','1-65000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','slowlog-log-slower-than','1000','0-60000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','slowlog-max-len','128','0-1000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','tcp-keepalive','0','0-2147483647');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','timeout','0','0,10-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','cluster-node-timeout','10000','1000-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','cluster-slave-validity-factor','10','0,1-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-3.2.11','save','\"900 1 300 10 60 10000\"','for example:\"900 1 300 10 60 10000\"');

insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','appendonly','yes','yes, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','appendfsync','everysec','everysec,always,no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','maxmemory-policy','noeviction','volatile-lru, allkeys-lru, volatile-random, allkeys-random, volatile');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','maxclients','10000','1-65000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','slowlog-log-slower-than','1000','0-60000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','slowlog-max-len','128','0-1000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','tcp-keepalive','0','0-2147483647');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','timeout','0','0,10-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','cluster-node-timeout','10000','1000-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','cluster-slave-validity-factor','10','0,1-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','save','\"900 1 300 10 60 10000\"','for example:\"900 1 300 10 60 10000\"');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-4.0.12','aof-use-rdb-preamble','yes','yes,no');

insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','appendonly','yes','yes, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','appendfsync','everysec','everysec, always, no');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','maxmemory-policy','noeviction','volatile-lru, allkeys-lru, volatile-random, allkeys-random, volatile');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','maxclients','10000','1-65000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','slowlog-log-slower-than','1000','0-60000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','slowlog-max-len','128','0-1000');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','tcp-keepalive','0','0-2147483647');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','timeout','0','0,10-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','cluster-node-timeout','10000','1000-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','cluster-replica-validity-factor','10','0,1-');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','save','\"900 1 300 10 60 10000\"','for example:\"900 1 300 10 60 10000\"');
insert into `config_dict` (`redis_mode`, `redis_version`, `item_name`, `default_item_value`, `item_comment`) values('cluster','redis-5.0.3','aof-use-rdb-preamble','yes','yes,no');