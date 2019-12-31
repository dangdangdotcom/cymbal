package com.dangdang.cymbal.service.operation.service.utility;

import com.dangdang.cymbal.domain.bo.InstanceBO;

import java.util.List;

/**
 *
 * @auther GeZhen
 */
public interface RedisReplicationUtilityService {

    /**
     * Refresh replication of redis server instances.
     *
     * @param instanceBOs
     * @return instance replication change or not
     */
    boolean refreshReplication(List<InstanceBO> instanceBOs);

    /**
     * Refresh replication of redis cluster.
     *
     * @param clusterId redis cluster id
     * @return cluster replication change or not
     */
    boolean refreshReplication(String clusterId);
}
