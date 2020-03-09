package com.dangdang.cymbal.service.cluster.service.utility.impl;

import com.dangdang.cymbal.common.enums.CapacityUnit;
import com.dangdang.cymbal.common.util.CollectionUtil;
import com.dangdang.cymbal.common.util.ThreadUtil;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.service.cluster.service.utility.DeploymentUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisOperationUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.enums.ShellCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Implement of {@link DeploymentUtilityService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class DeployUtilityServiceImpl implements DeploymentUtilityService {

    private static final int DEPLOY_WAIT_SECONDS = 2;

    @Resource
    private RedisShellUtilityService redisShellUtilityService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @Resource
    private RedisOperationUtilityService redisOperationUtilityService;

    @Override
    public void deploy(final ClusterBO redisClusterBO) {
        switch (redisClusterBO.getCluster().getRedisMode()) {
            case CLUSTER:
                deployForCluster(redisClusterBO);
                break;
            case STANDALONE:
                deployForStandAlone(redisClusterBO);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Redis mode must be %s or %s.", RedisMode.CLUSTER, RedisMode.STANDALONE));
        }
    }

    private void deployForCluster(final ClusterBO redisClusterBO) {
        deployAndStartupRedisServerInstances(redisClusterBO);
        createRedisCluster(redisClusterBO);
    }

    private void deployForStandAlone(final ClusterBO redisClusterBO) {
        deployAndStartupRedisServerInstances(redisClusterBO);
        configReplicationForStandAloneIfNeeded(redisClusterBO);
    }

    private void deployAndStartupRedisServerInstances(final ClusterBO redisClusterBO) {
        deployRedisServerInstances(redisClusterBO);
        ThreadUtil.sleep(DEPLOY_WAIT_SECONDS);
        redisClusterBO.getInstanceBOs().forEach(redisClientUtilityService::startup);
        ThreadUtil.sleep(DEPLOY_WAIT_SECONDS);
    }

    private void deployRedisServerInstances(final ClusterBO redisClusterBO) {
        redisClusterBO.getInstanceBOs().forEach(each -> {
            String ip = each.getNode().getIp();
            String port = each.getSelf().getPort().toString();
            String clusterId = each.getSelf().getClusterId();
            if (InstanceType.SENTINEL.equals(each.getSelf().getType())) {
                redisShellUtilityService.executeSentinelShellScript(each, ShellCommand.APPLY, ip, port, clusterId,
                        each.getMaster().getNode().getIp(), each.getMaster().getSelf().getPort().toString(),
                        String.valueOf(calculateSentinelQuorum(redisClusterBO.getCluster())));
            } else {
                redisShellUtilityService.executeRedisShellScript(each, ShellCommand.APPLY, ip, port, clusterId,
                        redisClusterBO.getCluster().getRedisMode().name().toLowerCase(),
                        redisClusterBO.getCluster().getCacheSize() + CapacityUnit.GB.name());
            }
        });
    }

    private int calculateSentinelQuorum(final Cluster cluster) {
        int redisInstanceCount = cluster.getMasterCount() + cluster.getReplicaCount();
        return redisInstanceCount / 2 + redisInstanceCount % 2;
    }

    private void createRedisCluster(final ClusterBO redisClusterBO) {
        StringBuilder redisServerInstanceUris = new StringBuilder();
        redisClusterBO.getInstanceBOs().forEach(each -> {
            redisServerInstanceUris.append(each.getNode().getIp()).append(":").append(each.getSelf().getPort())
                    .append(" ");
        });
        redisShellUtilityService.executeRedisShellScript(CollectionUtil.getFirst(redisClusterBO.getInstanceBOs()),
                ShellCommand.CREATE_CLUSTER, String.valueOf(redisClusterBO.getCluster().getReplicaCount()),
                redisServerInstanceUris.toString());
    }

    private void configReplicationForStandAloneIfNeeded(final ClusterBO redisClusterBO) {
        redisClusterBO.getInstanceBOs().forEach(each -> {
            if (InstanceType.REDIS.equals(each.getSelf().getType())) {
                if (each.getMaster() != null) {
                    redisClientUtilityService
                            .slaveOf(each, each.getMaster().getNode().getIp(),
                                    each.getMaster().getSelf().getPort().toString(),
                                    redisClusterBO.getCluster().getPassword());
                }
            }
        });
    }

    @Override
    public void deployForScaleOfCluster(final ClusterBO redisClusterBO, final InstanceBO presentServerInstance) {
        deployAndStartupRedisServerInstances(redisClusterBO);
        addRedisServerInstanceForCluster(redisClusterBO, presentServerInstance);
        configReplicationForClusterIfNeeded(redisClusterBO);
    }

    private void addRedisServerInstanceForCluster(final ClusterBO redisClusterBO,
            final InstanceBO presentServerInstance) {
        redisClusterBO.getInstanceBOs().forEach(each -> {
            redisShellUtilityService.executeRedisShellScript(each, ShellCommand.CLUSTER_MEET, each.getNode().getIp(),
                    each.getSelf().getPort().toString(), presentServerInstance.getNode().getIp(),
                    presentServerInstance.getSelf().getPort().toString());
        });
        ThreadUtil.sleep(DEPLOY_WAIT_SECONDS);
    }

    private void configReplicationForClusterIfNeeded(final ClusterBO redisClusterBO) {
        redisClusterBO.getInstanceBOs().stream().forEach(each -> {
            if (Objects.nonNull(each.getMaster())) {
                redisShellUtilityService
                        .executeRedisShellScript(each, ShellCommand.CLUSTER_REPLICATE, each.getNode().getIp(),
                                each.getSelf().getPort().toString(), each.getMaster().getNode().getIp(),
                                each.getMaster().getSelf().getPort().toString());
            }
        });
        ThreadUtil.sleep(DEPLOY_WAIT_SECONDS);
    }

    @Override
    public void deployForScaleOfStandalone(final ClusterBO redisClusterBO) {
        deployAndStartupRedisServerInstances(redisClusterBO);
        configReplicationForStandAloneIfNeeded(redisClusterBO);
    }
}
