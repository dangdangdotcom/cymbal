package com.dangdang.cymbal.service.cluster.service.process;


import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Instance;

import java.util.List;

/**
 * Process service for {@link Instance}.
 *
 * @auther GeZhen
 */
public interface InstanceProcessService {

    /**
     * Create some redis server instances for cluster.
     *
     * @param cluster redis cluster
     * @return
     */
    List<InstanceBO> createInstances(Cluster cluster);

    /**
     * Query redis cluster node id from redis server instance, and update to entity.
     *
     * @param redisClusterBO redisClusterBO
     */
    void queryAndUpdateRedisClusterNodeId(ClusterBO redisClusterBO);

    /**
     * Import redis server instance.
     * Query and save all redis server instance and node entities.
     *
     * @param redisClusterBO redisClusterBO
     */
    void importInstance(ClusterBO redisClusterBO);

    /**
     * Query and return redis server instance BOs by given cluster id.
     *
     * @param clusterId cluster id
     * @return redis server instance BOs
     */
    List<InstanceBO> queryInstanceBOsByClusterId(String clusterId);

    /**
     * Query and return redis server instance BOs by given node id.
     *
     * @param nodeId node id
     * @return redis server instance BOs
     */
    List<InstanceBO> queryInstanceBOsByNodeId(Integer nodeId);

    /**
     * Get redis server instance BO by instance id.
     *
     * @param instanceId redis server instance id
     * @return redis server instance BO
     */
    InstanceBO getInstanceBOById(Integer instanceId);
}
