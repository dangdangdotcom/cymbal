package com.dangdang.cymbal.service.util.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Shell execution exception.
 *
 * @auther GeZhen
 */
public class ShellExecutionException extends CymbalException {

    /**
     * Constructs an exception with formatted error message and arguments.
     *
     * @param errorMessage formatted error message
     * @param args         arguments of error message
     */
    public ShellExecutionException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    /**
     * Constructs an exception with error message and cause.
     *
     * @param cause
     * @param errorMessage
     * @param args
     */
    public ShellExecutionException(final Exception cause, final String errorMessage, final Object... args) {
        super(cause, errorMessage, args);
    }

    /**
     * Constructs an exception with cause.
     *
     * @param cause error cause
     */
    public ShellExecutionException(final Exception cause) {
        super(cause);
    }
}
