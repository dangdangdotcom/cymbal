package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Runtime status of redis server instance.
 *
 * @auther GeZhen
 */
public enum InstanceStatus implements IEnum<String> {

    STARTED,

    STOPPED;

    @Override
    public String getValue() {
        return this.name();
    }
}
