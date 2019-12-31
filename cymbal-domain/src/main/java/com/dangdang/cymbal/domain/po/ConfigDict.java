package com.dangdang.cymbal.domain.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConfigDict {

    private String id;

    private RedisMode redisMode;

    private String redisVersion;

    private String itemName;

    private String defaultItemValue;

    private String itemComment;
}
