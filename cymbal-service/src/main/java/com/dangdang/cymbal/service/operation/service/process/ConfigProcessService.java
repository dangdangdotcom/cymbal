package com.dangdang.cymbal.service.operation.service.process;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;

import java.util.List;


/**
 * Process service for config.
 *
 * @auther GeZhen
 */
public interface ConfigProcessService {

    /**
     * Create config for new cluster.
     *
     * @param cluster redis cluster
     * @param redisPersistenceType persistence type
     */
    void createConfigForNewRedisCluster(Cluster cluster, RedisPersistenceType redisPersistenceType);

    /**
     * Create config for imported cluster.
     * We will query config value from existed redis server instance, and merge it with default config, and save the result.
     *
     * @param redisClusterBO imported redis cluster BO
     */
    void createConfigForImportedRedisCluster(ClusterBO redisClusterBO);

    /**
     * Effect config details for new redis server instances when scale.
     *
     * @param scaledInstanceBOs scaled redis server instance BOs
     */
    void effectConfigForScaledInstances(List<InstanceBO> scaledInstanceBOs);

    /**
     * Query configs by user name.
     *
     * @param userName user name
     * @return redis configs
     */
    List<Config> queryByUserName(String userName);

    /**
     * Update redis config name.
     *
     * @param configId redis config id
     * @param newName new name of config
     */
    void updateConfigName(Integer configId, String newName);
}
