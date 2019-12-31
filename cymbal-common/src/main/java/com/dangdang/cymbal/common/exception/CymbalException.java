package com.dangdang.cymbal.common.exception;

/**
 * Base exception.
 *
 * @auther GeZhen
 */
public class CymbalException extends RuntimeException {

    /**
     * Constructs an exception with formatted error message and arguments.
     *
     * @param errorMessage formatted error message
     * @param args         arguments of error message
     */
    public CymbalException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    /**
     * Constructs an exception with error message and cause.
     *
     * @param cause
     * @param errorMessage
     * @param args
     */
    public CymbalException(final Throwable cause, final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args), cause);
    }

    /**
     * Constructs an exception with cause.
     *
     * @param cause error cause
     */
    public CymbalException(final Throwable cause) {
        super(cause);
    }
}
