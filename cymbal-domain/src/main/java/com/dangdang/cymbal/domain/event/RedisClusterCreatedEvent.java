package com.dangdang.cymbal.domain.event;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import org.springframework.context.ApplicationEvent;

/**
 * Event raised when a redis cluster created.
 *
 * @auther GeZhen
 */
public class RedisClusterCreatedEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param redisClusterBO created redis cluster
     */
    public RedisClusterCreatedEvent(final ClusterBO redisClusterBO) {
        super(redisClusterBO);
    }

    @Override
    public ClusterBO getSource() {
        return (ClusterBO) super.getSource();
    }
}
