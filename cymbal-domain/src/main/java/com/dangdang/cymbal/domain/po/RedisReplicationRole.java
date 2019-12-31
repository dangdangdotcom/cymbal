package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Replication role of redis.
 *
 * @auther GeZhen
 */
public enum RedisReplicationRole implements IEnum<String> {

    MASTER,

    SLAVE;

    @Override
    public String getValue() {
        return this.name();
    }
}
