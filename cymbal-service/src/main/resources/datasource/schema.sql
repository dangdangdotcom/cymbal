CREATE SCHEMA cymbal;
USE cymbal;

CREATE TABLE `node` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `env` VARCHAR(10) NOT NULL COMMENT '环境',
  `idc` VARCHAR(10) NOT NULL COMMENT '数据中心',
  `ip` VARCHAR(15) NOT NULL COMMENT 'IP地址',
  `host` VARCHAR(100) NOT NULL COMMENT '域名',
  `total_memory` INT NOT NULL COMMENT '总共内存',
  `free_memory` INT NOT NULL COMMENT '剩余内存',
  `password` VARCHAR(32) NOT NULL COMMENT 'root口令',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '主机初始化状态，0:未初始化，1:已初始化, 2:已下线',
  `description` VARCHAR(256) DEFAULT NULL COMMENT '记录一些特殊信息，例如维修历史等',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='主机';

CREATE TABLE `cluster` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `env` VARCHAR(10) NOT NULL COMMENT '环境',
  `idc` VARCHAR(10) NOT NULL COMMENT '数据中心',
  `cluster_id` CHAR(8) NOT NULL COMMENT '集群ID',
  `redis_mode` VARCHAR(16) NOT NULL COMMENT '集群模式',
  `redis_version` VARCHAR(16) NOT NULL COMMENT 'redis版本',
  `cache_size` INT NOT NULL COMMENT '内存大小',
  `master_count` INT NOT NULL COMMENT '主节点数量',
  `replica_count` INT NOT NULL COMMENT '备份节点数量',
  `enable_sentinel` TINYINT DEFAULT '0' COMMENT '是否开启sentinel监控',
  `password` VARCHAR(128) DEFAULT NULL COMMENT '密码',
  `description` VARCHAR(100) DEFAULT NULL COMMENT '描述',
  `user_name` VARCHAR(64) NOT NULL COMMENT '用户域账号',
  `user_cn_name` VARCHAR(64) NOT NULL COMMENT '用户中文名称',
  `status` VARCHAR(20) NOT NULL COMMENT '集群状态',
  `alarm_level` TINYINT NOT NULL DEFAULT 0 COMMENT '监控报警等级 0为最高级',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='redis集群';

CREATE TABLE `instance` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `node_id` INT NOT NULL COMMENT '所属主机',
  `port` SMALLINT NOT NULL COMMENT '服务端口',
  `cluster_id` VARCHAR(8) NOT NULL COMMENT '集群ID',
  `redis_version` VARCHAR(16) NOT NULL COMMENT 'redis版本',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '实例类型，0:redis，1:sentinel',
  `role` VARCHAR(8) DEFAULT NULL COMMENT 'redis角色：',
  `slaveof` VARCHAR(32) DEFAULT NULL,
  `cluster_node_id` VARCHAR(64) DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL,
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='redis实例';

CREATE TABLE `role_dict` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '角色ID，自增主键',
  `role_name` VARCHAR(255) NOT NULL COMMENT '角色名称',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='角色字典表';

CREATE TABLE `user_role` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_en_name` VARCHAR(64) NOT NULL COMMENT '用户英文名称(域账号)',
  `role_id` INT NOT NULL COMMENT '角色ID，自增主键',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户角色表';

CREATE TABLE `server_instance_permission` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `serv_inst_id` INT DEFAULT NULL COMMENT '主机实例ID',
  `belonged_role_id` INT DEFAULT NULL COMMENT '服务实例所属角色',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='服务实例权限表';

CREATE TABLE `application_form` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '申请单号，自增主键',
  `applicant_en_name` VARCHAR(64) NOT NULL COMMENT '申请人英文名称（域账号）',
  `applicant_cn_name` VARCHAR(64) NOT NULL COMMENT '申请人中文名称',
  `env` VARCHAR(10) NOT NULL COMMENT '环境类型',
  `idc` VARCHAR(10) NOT NULL COMMENT '数据中心',
  `redis_mode` VARCHAR(16) NOT NULL COMMENT 'redis模式',
  `redis_version` VARCHAR(16) NOT NULL COMMENT 'redis版本',
  `cache_size` SMALLINT NOT NULL COMMENT '每个节点的内存大小',
  `master_count` SMALLINT NOT NULL COMMENT '主节点数量',
  `replica_count` TINYINT NOT NULL COMMENT '每个主节点的从节点数量',
  `redis_persistence_type` VARCHAR(10) NOT NULL COMMENT '持久化类型',
  `enable_sentinel` TINYINT DEFAULT '0' COMMENT '是否开启sentinel监控，0：不开启，1：开启',
  `password` VARCHAR(128) DEFAULT NULL COMMENT '密码',
  `belong_system` VARCHAR(64) NOT NULL COMMENT '所属系统',
  `description` VARCHAR(100) NOT NULL COMMENT '描述',
  `status` TINYINT NOT NULL DEFAULT '0' COMMENT '状态，0：草稿，1：申请中，2：审核通过，3：驳回',
  `approval_opinion` VARCHAR(1024) DEFAULT NULL COMMENT '审批意见',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='Redis资源申请单表';

CREATE TABLE `config_dict` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `redis_mode` VARCHAR(16) NOT NULL COMMENT '集群模式, standalone or cluster',
  `redis_version` VARCHAR(16) NOT NULL COMMENT 'redis版本',
  `item_name` VARCHAR(64) NOT NULL COMMENT '配置项名称',
  `default_item_value` VARCHAR(64) NOT NULL COMMENT '配置项值',
  `item_comment` VARCHAR(256) NOT NULL COMMENT '配置说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='redis配置项字典表';

CREATE TABLE `config` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `config_name` VARCHAR(64) DEFAULT NULL COMMENT '配置项名称',
  `user_name` VARCHAR(64) DEFAULT NULL COMMENT '配置所属用户',
  `user_cn_name` VARCHAR(64) DEFAULT NULL COMMENT '配置所属用户中文名称',
  `cluster_id` CHAR(8) DEFAULT NULL COMMENT '集群ID',
  `redis_version` VARCHAR(16) DEFAULT NULL COMMENT 'redis版本',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='redis集群配置主表';

CREATE TABLE `config_detail` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `config_id` INT NOT NULL COMMENT '自增主键',
  `item_name` VARCHAR(64) DEFAULT NULL COMMENT '配置项名称',
  `item_value` VARCHAR(64) DEFAULT NULL COMMENT '配置项值',
  `status` TINYINT DEFAULT '0' COMMENT '状态 0:未生效,1:已生效',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='redis集群配置明细表';

CREATE TABLE `cluster_scale` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cluster_id` CHAR(8) NOT NULL COMMENT '集群ID',
  `type` TINYINT NOT NULL COMMENT '扩缩容类型：0:水平扩容、1:垂直扩容、2:增加从节点',
  `scale_num` INT NOT NULL COMMENT '扩容数值，针对不同扩容类型表达含义不同',
  `status` TINYINT NOT NULL COMMENT '扩容状态：0:进行中、1:已完成',
  `result` TINYINT DEFAULT NULL COMMENT '扩容结果：0:失败、1:成功',
  `result_desc` VARCHAR(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '结果备注',
  `operator` VARCHAR(64) NOT NULL COMMENT '操作人',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='Redis集群扩容信息表';

CREATE TABLE `cluster_permission` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `cluster_id` CHAR(8) NOT NULL COMMENT '被授权cluster_id',
  `user_name` VARCHAR(64) NOT NULL COMMENT '被授权人域账号',
  `user_cn_name` VARCHAR(64) NOT NULL COMMENT '被授权人中文名',
  `creation_date` DATETIME NOT NULL COMMENT '建立日期',
  `last_changed_date` DATETIME NOT NULL COMMENT '末次修改时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='redis集群权限管理';

create table `user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_name` VARCHAR(64) NOT NULL COMMENT '英文名（用于登陆）',
  `user_cn_name` VARCHAR(64) NOT NULL COMMENT '中文名',
  `email` VARCHAR(128) NOT NULL COMMENT '邮箱地址',
  `password` VARCHAR(128) NOT NULL COMMENT '密码',
  `creation_date` DATETIME NOT NULL COMMENT '创建时间',
  `last_changed_date` DATETIME NOT NULL COMMENT '最后修改时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息';