package com.dangdang.cymbal.service.util.service;

import org.springframework.context.ApplicationEvent;

/**
 * Service about event.
 * such as publish, and so on.
 *
 * @auther GeZhen
 */
public interface EventService {

    /**
     * Publish a spring application event.
     *
     * @param event event
     */
    void publish(ApplicationEvent event);
}
