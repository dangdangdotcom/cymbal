package com.dangdang.cymbal.service.operation.service.process.impl;

import com.dangdang.cymbal.common.util.CollectionUtil;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.operation.service.entity.ConfigEntityService;
import com.dangdang.cymbal.service.operation.service.process.ConfigDetailProcessService;
import com.dangdang.cymbal.service.operation.service.process.ConfigProcessService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Implement of {@link ConfigProcessService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class ConfigProcessServiceImpl implements ConfigProcessService {

    private final static String REDIS_CONFIG_SUFFIX = "redis";

    private final static String SENTINEL_CONFIG_SUFFIX = "sentinel";

    @Resource
    private ConfigEntityService redisConfigEntityService;

    @Resource
    private ConfigDetailProcessService redisConfigDetailProcessService;

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Override
    public void createConfigForNewRedisCluster(final Cluster cluster, final RedisPersistenceType redisPersistenceType) {
        Config config = createDefaultRedisConfig(cluster);
        if (cluster.isEnableSentinel()) {
            createDefaultSentinelConfig(cluster);
        }
        redisConfigDetailProcessService.updatePersistenceType(config, redisPersistenceType);
    }

    private Config createDefaultRedisConfig(final Cluster cluster) {
        return createDefaultConfig(cluster, REDIS_CONFIG_SUFFIX, cluster.getRedisVersion());
    }

    private Config createDefaultSentinelConfig(final Cluster cluster) {
        return createDefaultConfig(cluster, SENTINEL_CONFIG_SUFFIX, Constant.RedisConfig.SENTINEL_CONFIG_VERSION);
    }

    private Config createDefaultConfig(final Cluster cluster, final String configNameSuffix, final String version) {
        Config redisClusterConfig = new Config();
        redisClusterConfig.setConfigName(String.format("%s-%s", cluster.getDescription(), configNameSuffix));
        redisClusterConfig.setRedisVersion(version);
        redisClusterConfig.setClusterId(cluster.getClusterId());
        redisClusterConfig.setUserName(cluster.getUserName());
        redisClusterConfig.setUserCnName(cluster.getUserCnName());
        redisClusterConfig.setLastChangedDate(new Date());
        redisClusterConfig.setCreationDate(new Date());
        redisConfigEntityService.save(redisClusterConfig);
        return redisClusterConfig;
    }

    @Override
    public void createConfigForImportedRedisCluster(final ClusterBO redisClusterBO) {
        Config config = createDefaultRedisConfig(redisClusterBO.getCluster());
        redisConfigDetailProcessService.createConfigDetailsForImportedRedisCluster(config, redisClusterBO);
    }

    @Override
    public void effectConfigForScaledInstances(final List<InstanceBO> scaledInstanceBOs) {
        Config config = redisConfigEntityService
                .getByClusterIdOfRedis(CollectionUtil.getFirst(scaledInstanceBOs).getSelf().getClusterId());
        redisConfigDetailProcessService.effectConfigDetailsForScaledInstance(config, scaledInstanceBOs);
    }

    @Override
    public List<Config> queryByUserName(String userName) {
        if (userRoleProcessService.isAdmin(userName)) {
            return redisConfigEntityService.list();
        } else {
            return redisConfigEntityService.queryByUserName(userName);
        }
    }

    @Override
    public void updateConfigName(Integer configId, String newName) {
        Config config = redisConfigEntityService.getById(configId);
        Preconditions.checkNotNull(configId);
        config.setConfigName(newName);
        redisConfigEntityService.updateById(config);
    }
}
