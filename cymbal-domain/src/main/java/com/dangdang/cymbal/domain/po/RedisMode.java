package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Redis mode.
 *
 * @auther GeZhen
 */
public enum RedisMode implements IEnum<String> {

    STANDALONE,

    CLUSTER,

    SENTINEL;

    @Override
    public String getValue() {
        return this.name();
    }
}
