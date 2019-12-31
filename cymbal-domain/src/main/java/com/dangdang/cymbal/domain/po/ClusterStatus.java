package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Redis cluster status.
 *
 * @auther GeZhen
 */
public enum ClusterStatus implements IEnum<String> {

    UP,

    DOWN;

    @Override
    public String getValue() {
        return this.name();
    }
}
