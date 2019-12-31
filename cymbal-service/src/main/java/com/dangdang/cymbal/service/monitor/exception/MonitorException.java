package com.dangdang.cymbal.service.monitor.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Monitor exception.
 *
 * @auther GeZhen
 */
public class MonitorException extends CymbalException {

    public MonitorException(String errorMessage, Object... args) {
        super(errorMessage, args);
    }

    public MonitorException(Exception cause) {
        super(cause);
    }
}
