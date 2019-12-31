package com.dangdang.cymbal.service.util.service.impl;

import com.dangdang.cymbal.service.util.service.EventService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Implement of {@link EventService}.
 *
 * @auther GeZhen
 */
@Service
public class EventServiceImpl implements EventService {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void publish(final ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }
}
