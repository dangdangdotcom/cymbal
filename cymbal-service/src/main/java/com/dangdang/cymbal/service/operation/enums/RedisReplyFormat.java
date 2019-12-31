package com.dangdang.cymbal.service.operation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis reply format.
 *
 * @author GeZhen
 */
@AllArgsConstructor
@Getter
public enum RedisReplyFormat {

    RAW("raw"),

    NO_RAW("no-raw");

    private String value;
}
