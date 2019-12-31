package com.dangdang.cymbal.service.operation.service.process;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ConfigDetailBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;

import java.util.List;

/**
 * Process service for {@link ConfigDetail}.
 *
 * @auther GeZhen
 */
public interface ConfigDetailProcessService {

    /**
     * Update persistence type for redis.
     *
     * @param config redis config
     * @param redisPersistenceType redis persistence type
     */
    void updatePersistenceType(Config config, RedisPersistenceType redisPersistenceType);

    /**
     * Effect all not effective config details under a config.
     *
     * @param config redis config
     */
    void effectConfigDetails(Config config);

    /**
     * Effect all not effective config details under a config id.
     *
     * @param configId redis config id
     */
    void effectConfigDetails(Integer configId);

    /**
     * Create redis config details for imported redis cluster.
     * We will query config details from existed redis server instance, and save it.
     *
     * @param config redis config
     * @param redisClusterBO imported redis cluster
     */
    void createConfigDetailsForImportedRedisCluster(Config config, ClusterBO redisClusterBO);

    /**
     * Get redis config detail of redis server by config id and item name.
     *
     * @param clusterId cluster id
     * @param itemName item name of config
     * @return redis config detail
     */
    ConfigDetail getByClusterIdAndItemName(String clusterId, String itemName);

    /**
     * Effect config details to new redis server instances of scale.
     *
     * @param scaledInstanceBOs scaled redis server instances
     */
    void effectConfigDetailsForScaledInstance(Config config, List<InstanceBO> scaledInstanceBOs);

    /**
     * Create or update a redis config detail.
     *
     * @param configDetail redis config detail
     * @return redis config detail id
     */
    Integer createOrUpdateConfigDetail(ConfigDetail configDetail);

    /**
     * Query redis config details by config id.
     * Result will be merge with default config details of target version of redis.
     *
     * @param configId
     * @return
     */
    List<ConfigDetailBO> queryByConfigId(Integer configId);
}
