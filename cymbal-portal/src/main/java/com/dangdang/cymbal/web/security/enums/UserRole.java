package com.dangdang.cymbal.web.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @auther GeZhen
 */
@Getter
@AllArgsConstructor
public enum UserRole {

    USER("ROLE_USER"),

    ADMIN("ROLE_ADMIN"),

    ;

    private String value;
}
