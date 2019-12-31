package com.dangdang.cymbal.service.cluster.service.utility;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;

/**
 * Service for deploy redis.
 *
 * @auther GeZhen
 */
public interface DeploymentUtilityService {

    /**
     * Deploy a redis cluster.
     *
     * @param redisClusterBO redis cluster
     */
    void deploy(ClusterBO redisClusterBO);

    /**
     * Deploy new instance for cluster scale.
     * Every redis server instance in redisClusterBO will be deploy.
     *
     * @param redisClusterBO redis cluster of scale
     * @param presentServerInstance present server instance of cluster
     */
    void deployForScaleOfCluster(ClusterBO redisClusterBO, InstanceBO presentServerInstance);

    /**
     * Deploy new instance for standalone scale.
     * Every redis server instance in redisClusterBO will be deploy.
     *
     * @param redisClusterBO redis cluster of scale
     */
    void deployForScaleOfStandalone(ClusterBO redisClusterBO);
}
