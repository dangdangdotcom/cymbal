package com.dangdang.cymbal.service.operation.service.process.impl;

import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.operation.enums.RedisCommand;
import com.dangdang.cymbal.service.operation.enums.RedisConfigItem;
import com.dangdang.cymbal.service.operation.enums.RedisReplyFormat;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDetailEntityService;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDictEntityService;
import com.dangdang.cymbal.service.operation.service.entity.ConfigEntityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.common.util.CollectionUtil;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ConfigDetailBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.domain.po.ConfigDetailStatus;
import com.dangdang.cymbal.domain.po.ConfigDict;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;
import com.dangdang.cymbal.service.operation.service.process.ConfigDetailProcessService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implement of {@link ConfigDetailProcessService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class ConfigDetailProcessServiceImpl implements ConfigDetailProcessService {

    private static final String CONFIG_NAME_SAVE = "save";

    @Resource
    private ConfigDetailEntityService redisConfigDetailEntityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Resource
    private ConfigDictEntityService redisConfigDictEntityService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @Resource
    private ConfigEntityService redisConfigEntityService;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Override
    public void updatePersistenceType(final Config config, final RedisPersistenceType redisPersistenceType) {
        if (redisPersistenceType.equals(RedisPersistenceType.NO) || redisPersistenceType
                .equals(redisPersistenceType.AOF)) {
            createRedisConfigDetail(config.getId(), RedisConfigItem.SAVE.name().toLowerCase(),
                    Constant.RedisConfig.SAVE_OFF);
        }
        if (redisPersistenceType.equals(RedisPersistenceType.NO) || redisPersistenceType
                .equals(RedisPersistenceType.RDB)) {
            createRedisConfigDetail(config.getId(), RedisConfigItem.APPENDONLY.name().toLowerCase(),
                    Constant.RedisConfig.APPENDONLY_NO);
        }
        this.effectConfigDetails(config);
    }

    private ConfigDetail createRedisConfigDetail(final Integer configId, final String itemName,
            final String itemValue) {
        ConfigDetail configDetail = new ConfigDetail();
        configDetail.setConfigId(configId);
        configDetail.setStatus(ConfigDetailStatus.NOT_EFFECTIVE);
        configDetail.setItemName(itemName);
        configDetail.setItemValue(itemValue);
        redisConfigDetailEntityService.save(configDetail);
        return configDetail;
    }

    @Override
    public void effectConfigDetails(final Config config) {
        effectConfigDetails(config, queryRedisServerInstancesToEffect(config));
    }

    private void effectConfigDetails(final Config config, final List<InstanceBO> instanceBOs) {
        List<ConfigDetail> notEffectiveConfigDetails = queryNotEffectiveRedisConfigDetails(config);
        if (!notEffectiveConfigDetails.isEmpty()) {
            effectConfigDetails(notEffectiveConfigDetails, instanceBOs);
        }
    }

    private List<ConfigDetail> queryNotEffectiveRedisConfigDetails(final Config config) {
        return redisConfigDetailEntityService.lambdaQuery().eq(ConfigDetail::getConfigId, config.getId())
                .eq(ConfigDetail::getStatus, ConfigDetailStatus.NOT_EFFECTIVE).list();
    }

    private List<InstanceBO> queryRedisServerInstancesToEffect(final Config config) {
        List<InstanceBO> instanceBOS = instanceProcessService.queryInstanceBOsByClusterId(config.getClusterId());
        // TODO: Butter way.
        if (isSentinelConfig(config)) {
            return instanceBOS.stream().filter(each -> InstanceType.SENTINEL.equals(each.getSelf().getType()))
                    .collect(Collectors.toList());
        } else {
            return instanceBOS.stream().filter(each -> InstanceType.REDIS.equals(each.getSelf().getType()))
                    .collect(Collectors.toList());
        }
    }

    private boolean isSentinelConfig(final Config config) {
        return Constant.RedisConfig.SENTINEL_CONFIG_VERSION.equals(config.getRedisVersion());
    }

    private void effectConfigDetails(final List<ConfigDetail> configDetails, final List<InstanceBO> instanceBOs) {
        configDetails.forEach(redisConfigDetails -> {
            instanceBOs.forEach(each -> {
                redisClientUtilityService
                        .configSet(each, redisConfigDetails.getItemName(), redisConfigDetails.getItemValue());
            });
            redisConfigDetails.setStatus(ConfigDetailStatus.EFFECTIVE);
            redisConfigDetailEntityService.updateById(redisConfigDetails);
        });
    }

    @Override
    public void effectConfigDetails(Integer configId) {
        Config config = redisConfigEntityService.getById(configId);
        this.effectConfigDetails(config);
    }

    @Override
    public void createConfigDetailsForImportedRedisCluster(final Config config, final ClusterBO redisClusterBO) {
        pullAndCreateConfigDetails(config, redisClusterBO);
        effectConfigDetails(config, redisClusterBO.getInstanceBOs());
    }

    private void pullAndCreateConfigDetails(final Config config, final ClusterBO redisClusterBO) {
        Map<String, String> realRedisConfigs = pullRedisConfig(redisClusterBO);
        createNoneDefaultConfigDetails(config, realRedisConfigs, redisClusterBO.getCluster());
    }

    private Map<String, String> pullRedisConfig(final ClusterBO redisClusterBO) {
        InstanceBO instanceBO = CollectionUtil.getFirst(redisClusterBO.getInstanceBOs());
        List<String> configs = redisClientUtilityService
                .executeRedisCommand(instanceBO, String.format("%s \\*", RedisCommand.CONFIG_GET.getValue()),
                        RedisReplyFormat.RAW);
        return CollectionUtil.toMap(configs);
    }

    private void createNoneDefaultConfigDetails(final Config config, final Map<String, String> realRedisConfigs,
            final Cluster cluster) {
        List<ConfigDict> redisConfigDicts = queryDefaultConfigs(cluster.getRedisMode(), cluster.getRedisVersion());
        createNoneDefaultConfigDetails(config, realRedisConfigs, redisConfigDicts);
    }

    private List<ConfigDict> queryDefaultConfigs(final Integer configId) {
        Config config = redisConfigEntityService.getById(configId);
        Cluster cluster = clusterEntityService.getByClusterId(config.getClusterId());
        return queryDefaultConfigs(cluster.getRedisMode(), cluster.getRedisVersion());
    }

    private List<ConfigDict> queryDefaultConfigs(final RedisMode redisMode, final String redisVersion) {
        return redisConfigDictEntityService.lambdaQuery().eq(ConfigDict::getRedisMode, redisMode)
                .eq(ConfigDict::getRedisVersion, redisVersion).list();
    }

    private void createNoneDefaultConfigDetails(final Config config, final Map<String, String> realRedisConfigs,
            final List<ConfigDict> redisConfigDicts) {
        redisConfigDicts.forEach(each -> {
            String configName = each.getItemName();
            if (realRedisConfigs.containsKey(configName)) {
                String realConfigValue = realRedisConfigs.get(configName);
                if (CONFIG_NAME_SAVE.equals(configName)) {
                    realConfigValue = String.format("\"%s\"", realConfigValue);
                }
                if (!realConfigValue.equals(each.getDefaultItemValue())) {
                    createRedisConfigDetail(config.getId(), configName, realConfigValue);
                }
            }
        });
    }

    @Override
    public ConfigDetail getByClusterIdAndItemName(String clusterId, String itemName) {
        Config config = redisConfigEntityService.getByClusterIdOfRedis(clusterId);
        Preconditions.checkNotNull(config);
        return redisConfigDetailEntityService.getByConfigIdAndItemName(config.getId(), itemName);
    }

    @Override
    public void effectConfigDetailsForScaledInstance(final Config config, final List<InstanceBO> scaledInstanceBOs) {
        List<ConfigDetail> configDetails = queryEffectivedRedisConfigDetails(config);
        effectConfigDetails(configDetails, scaledInstanceBOs);
    }

    List<ConfigDetail> queryEffectivedRedisConfigDetails(final Config config) {
        return redisConfigDetailEntityService.lambdaQuery().eq(ConfigDetail::getConfigId, config.getId())
                .eq(ConfigDetail::getStatus, ConfigDetailStatus.EFFECTIVE).list();
    }

    @Override
    public Integer createOrUpdateConfigDetail(final ConfigDetail configDetail) {
        // TODO 优化
        if (Objects.isNull(configDetail.getId())) {
            configDetail.setStatus(ConfigDetailStatus.NOT_EFFECTIVE);
            configDetail.setCreationDate(new Date());
            configDetail.setLastChangedDate(configDetail.getCreationDate());
            redisConfigDetailEntityService.save(configDetail);
        } else {
            ConfigDetail oldConfigDetail = redisConfigDetailEntityService.getById(configDetail.getId());
            oldConfigDetail.setStatus(ConfigDetailStatus.NOT_EFFECTIVE);
            oldConfigDetail.setItemName(configDetail.getItemName());
            redisConfigDetailEntityService.saveOrUpdate(configDetail);
        }
        return configDetail.getId();
    }

    @Override
    public List<ConfigDetailBO> queryByConfigId(final Integer configId) {
        List<ConfigDetail> configDetails = redisConfigDetailEntityService.queryByConfigId(configId);
        return mergeWithDefaultConfigs(configId, configDetails);
    }

    private List<ConfigDetailBO> mergeWithDefaultConfigs(final Integer configId,
            final List<ConfigDetail> configDetails) {
        List<ConfigDict> defaultConfigs = queryDefaultConfigs(configId);
        return mergeWithDefaultConfigs(configDetails, defaultConfigs);
    }

    private List<ConfigDetailBO> mergeWithDefaultConfigs(final List<ConfigDetail> configDetails,
            final List<ConfigDict> defaultConfigs) {
        List<ConfigDetailBO> configDetailBOS = new ArrayList<>();
        defaultConfigs.forEach(eachDict -> {
            boolean contains = false;
            for (ConfigDetail configDetail : configDetails) {
                if (configDetail.getItemName().equals(eachDict.getItemName())) {
                    configDetailBOS.add(buildRedisConfigDetailBO(configDetail, eachDict));
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                configDetailBOS.add(buildRedisConfigDetailBO(null, eachDict));
            }
        });
        return configDetailBOS;
    }

    private ConfigDetailBO buildRedisConfigDetailBO(final ConfigDetail configDetail, final ConfigDict redisConfigDict) {
        return ConfigDetailBO.builder().configDetail(configDetail).configDict(redisConfigDict).build();
    }
}
