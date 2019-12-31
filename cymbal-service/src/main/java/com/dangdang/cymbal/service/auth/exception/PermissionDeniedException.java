package com.dangdang.cymbal.service.auth.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Thrown when permission denied.
 *
 * @auther GeZhen
 */
public class PermissionDeniedException extends CymbalException {

    public PermissionDeniedException(String errorMessage, Object... args) {
        super(errorMessage, args);
    }

    public PermissionDeniedException(Exception cause, String errorMessage, Object... args) {
        super(cause, errorMessage, args);
    }

    public PermissionDeniedException(Exception cause) {
        super(cause);
    }
}
