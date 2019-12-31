package com.dangdang.cymbal.service.operation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum of redis commands.
 *
 * @auther GeZhen
 */
@AllArgsConstructor
@Getter
public enum RedisCommand {

    CLUSTER_NODES("cluster nodes"),

    CONFIG_GET("config get"),

    BGSAVE("bgsave"),

    INFO_PERSISTENCE("info persistence"),

    SHUTDOWN("shutdown"),

    CLUSTER_FAILOVER("cluster failover"),

    ;

    private String value;
}
