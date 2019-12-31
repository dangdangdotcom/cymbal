package com.dangdang.cymbal.service.node.exception;

import com.dangdang.cymbal.common.exception.CymbalException;

/**
 * Thrown when resources are not enough to be assigned.
 *
 * @author GeZhen
 */
public class NotEnoughResourcesException extends CymbalException {

    public NotEnoughResourcesException(String errorMessage, Object... args) {
        super(errorMessage, args);
    }
}
