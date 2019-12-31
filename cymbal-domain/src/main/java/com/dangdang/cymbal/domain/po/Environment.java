package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Environment(profile) for node.
 *
 * @author GeZhen
 */
public enum Environment implements IEnum<String> {

    TEST,

    STAGING,

    PRODUCTION;

    @Override
    public String getValue() {
        return this.name();
    }
}
