package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Persistence type of redis.
 *
 * @author GeZhen
 */
public enum RedisPersistenceType implements IEnum<String> {

    NO,

    RDB,

    AOF;

    @Override
    public String getValue() {
        return this.name();
    }
}
