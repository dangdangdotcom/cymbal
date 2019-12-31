package com.dangdang.cymbal.job.persistence.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Throws when persistence timeout.
 *
 * @auther GeZhen
 */
public class PersistenceTimeoutException extends CymbalException {

    public PersistenceTimeoutException(final String errorMessage, final Object... args) {
        super(errorMessage, args);
    }

    public PersistenceTimeoutException(final Exception cause, final String errorMessage, final Object... args) {
        super(cause, errorMessage, args);
    }

    public PersistenceTimeoutException(final Exception cause) {
        super(cause);
    }
}
