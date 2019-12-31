package com.dangdang.cymbal.service.node.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Thrown then parse excel file fail.
 *
 * @auther GeZhen
 */
public class ParseExcelFileException extends CymbalException {

    /**
     * Constructs an exception with formatted error message and arguments.
     *
     * @param errorMessage formatted error message
     * @param args         arguments of error message
     */
    public ParseExcelFileException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    /**
     * Constructs an exception with error message and cause.
     *
     * @param message error message
     * @param cause   error cause
     */
    public ParseExcelFileException(final String message, final Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs an exception with cause.
     *
     * @param cause error cause
     */
    public ParseExcelFileException(final Exception cause) {
        super(cause);
    }
}
