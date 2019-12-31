package com.dangdang.cymbal.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum of capacity unit.
 *
 * @auther GeZhen
 */
@Getter
@AllArgsConstructor
public enum CapacityUnit {

    KB(1024),

    MB(1024 * 1024),

    GB(1024 * 1024 * 1024);

    private long bytes;
}
