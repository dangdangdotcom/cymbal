package com.dangdang.cymbal.service.util.enums;

import lombok.Getter;

/**
 * Ansible playbook name.
 *
 * @author GeZhen
 */
@Getter
public enum AnsiblePlayBookName {

    TOTAL_INIT("total_init"),

    MONITOR_INIT("monitor_init");

    private String value;

    AnsiblePlayBookName(final String value) {
        this.value = value;
    }
}
