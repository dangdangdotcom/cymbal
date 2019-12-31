package com.dangdang.cymbal.service.operation.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Thrown when scale exception.
 *
 * @auther GeZhen
 */
public class ScaleException extends CymbalException {

    public ScaleException(String errorMessage, Object... args) {
        super(errorMessage, args);
    }

    public ScaleException(Throwable cause, String errorMessage, Object... args) {
        super(cause, errorMessage, args);
    }

    public ScaleException(Throwable cause) {
        super(cause);
    }
}
