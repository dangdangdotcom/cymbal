package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * IDCs for node.
 *
 * @author GeZhen
 */
public enum InternetDataCenter implements IEnum<String> {

    TEST,

    IDC4,

    IDC5,

    IDC7;

    @Override
    public String getValue() {
        return this.name();
    }
}
