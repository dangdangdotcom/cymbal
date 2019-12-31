package com.dangdang.cymbal.service.monitor.service;

/**
 * Service to handle alert.
 *
 * @auther GeZhen
 */
public interface AlertManagerService {

    /**
     * Handle alert message from any monitor system.
     *
     * @param alertInfo alert info
     */
    void handleAlert(Object alertInfo);
}
