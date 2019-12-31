package com.dangdang.cymbal.service.cluster.service.process;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.Cluster;

import java.util.List;

/**
 * Process service for redis cluster.
 *
 * @auther GeZhen
 */
public interface ClusterProcessService {

    /**
     * Create a redis cluster.
     * Include insert entity, deploy redis server instance on node, and let then into a cluster, and so on.
     *
     * @param applicationForm application form to create cluster
     * @return cluster id
     */
    String createRedisClusterByRedisApplicationForm(ApplicationForm applicationForm);

    /**
     * Import a exist redis cluster.
     * Include init nodes, deploy run time directory, init monitor, and so on.
     *
     * @param redisClusterBO redis cluster to import
     * @return cluster id
     */
    String createRedisClusterByImport(ClusterBO redisClusterBO);

    /**
     * Query all redis cluster.
     *
     * @return redis cluster BOs
     */
    List<ClusterBO> queryAllRedisClusters();

    /**
     * Query redis clusters which are created by user or granted permission to user.
     *
     * @param userName user name
     * @return redis clusters
     */
    List<Cluster> queryByUserName(String userName);

    /**
     * Get redis cluster BO by cluster Id.
     *
     * @param clusterId cluster id
     * @return cluster BO
     */
    ClusterBO getRedisClusterById(String clusterId);
}
