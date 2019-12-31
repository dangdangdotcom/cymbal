package com.dangdang.cymbal.service.operation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for redis config item.
 *
 * @auther GeZhen
 */
@Getter
@AllArgsConstructor
public enum RedisConfigItem {

    APPENDONLY("appendonly"),

    SAVE("save"),

    MAXMEMORY("maxmemory"),

    ;

    private String value;
}
