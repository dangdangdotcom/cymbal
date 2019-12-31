package com.dangdang.cymbal.service.cluster.service.process.impl;

import com.dangdang.cymbal.common.enums.CapacityUnit;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.event.RedisClusterCreatedEvent;
import com.dangdang.cymbal.domain.po.AlarmLevel;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterPermission;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.auth.service.entity.ClusterPermissionEntityService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.cluster.service.utility.DeploymentUtilityService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.monitor.service.MonitorService;
import com.dangdang.cymbal.service.operation.service.process.ConfigProcessService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.RedisUtil;
import com.dangdang.cymbal.service.util.enums.ShellCommand;
import com.dangdang.cymbal.service.util.service.EventService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implement of {@link ClusterProcessService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class ClusterProcessServiceImpl implements ClusterProcessService {

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Resource
    private ConfigProcessService redisConfigProcessService;

    @Resource
    private DeploymentUtilityService deploymentUtilityService;

    @Resource
    private MonitorService monitorService;

    @Resource
    private EventService eventService;

    @Resource
    private RedisShellUtilityService redisShellUtilityService;

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Resource
    private ClusterPermissionEntityService clusterPermissionEntityService;

    @Override
    public String createRedisClusterByRedisApplicationForm(final ApplicationForm applicationForm) {
        Preconditions.checkArgument(applicationForm != null, "Redis application form is required.");
        Cluster cluster = createRedisClusterEntityByRedisApplicationForm(applicationForm);
        ClusterBO redisClusterBO = createRedisCluster(cluster);
        redisConfigProcessService.createConfigForNewRedisCluster(cluster, applicationForm.getRedisPersistenceType());
        monitorService.initMonitorForCluster(redisClusterBO);
        publishRedisClusterCreatedEvent(redisClusterBO);
        return cluster.getClusterId();
    }

    private Cluster createRedisClusterEntityByRedisApplicationForm(final ApplicationForm applicationForm) {
        Cluster cluster = new Cluster();
        BeanUtils.copyProperties(applicationForm, cluster);
        cluster.setUserName(applicationForm.getApplicantEnName());
        cluster.setUserCnName(applicationForm.getApplicantCnName());
        cluster.setDescription(applicationForm.getBelongSystem());
        cluster.setClusterId(RedisUtil.generateClusterId());
        // TODO: Allow user change alarm level.
        cluster.setAlarmLevel(AlarmLevel.ALARM);
        cluster.setStatus(ClusterStatus.UP);
        clusterEntityService.save(cluster);
        return cluster;
    }

    private ClusterBO createRedisCluster(final Cluster cluster) {
        List<InstanceBO> instanceBOS = instanceProcessService.createInstances(cluster);
        ClusterBO redisClusterBO = createRedisClusterBO(cluster, instanceBOS);
        deploymentUtilityService.deploy(redisClusterBO);
        queryAndUpdateClusterNodeIdIfNeeded(redisClusterBO);
        return redisClusterBO;
    }

    private ClusterBO createRedisClusterBO(final Cluster cluster, final List<InstanceBO> instanceBOs) {
        return ClusterBO.builder().cluster(cluster).instanceBOs(instanceBOs).build();
    }

    private void queryAndUpdateClusterNodeIdIfNeeded(final ClusterBO redisClusterBO) {
        if (redisClusterBO.getCluster().getRedisMode().equals(RedisMode.CLUSTER)) {
            instanceProcessService.queryAndUpdateRedisClusterNodeId(redisClusterBO);
        }
    }

    private void publishRedisClusterCreatedEvent(final ClusterBO redisClusterBO) {
        RedisClusterCreatedEvent redisClusterCreatedEvent = new RedisClusterCreatedEvent(redisClusterBO);
        eventService.publish(redisClusterCreatedEvent);
    }

    @Override
    @Transactional
    public String createRedisClusterByImport(final ClusterBO redisClusterBO) {
        saveRedisCluster(redisClusterBO.getCluster());
        instanceProcessService.importInstance(redisClusterBO);
        executeImportShellScript(redisClusterBO);
        monitorService.initMonitorForCluster(redisClusterBO);
        redisConfigProcessService.createConfigForImportedRedisCluster(redisClusterBO);
        publishRedisClusterCreatedEvent(redisClusterBO);
        return redisClusterBO.getCluster().getClusterId();
    }

    private void saveRedisCluster(final Cluster cluster) {
        Preconditions.checkArgument(Objects.nonNull(cluster));
        cluster.setClusterId(RedisUtil.generateClusterId());
        clusterEntityService.save(cluster);
    }

    private void executeImportShellScript(final ClusterBO redisClusterBO) {
        Cluster cluster = redisClusterBO.getCluster();
        redisClusterBO.getInstanceBOs().forEach(each -> {
            redisShellUtilityService.executeRedisShellScript(each, ShellCommand.IMPORT, each.getNode().getIp(),
                    each.getSelf().getPort().toString(), redisClusterBO.getCluster().getClusterId(),
                    cluster.getRedisMode().name().toLowerCase(),
                    String.format("%d%s", cluster.getCacheSize(), CapacityUnit.GB),
                    getRedisConfigFormatSlaveOf(cluster.getRedisMode(), each.getSelf()));
        });
    }

    private String getRedisConfigFormatSlaveOf(final RedisMode redisMode, final Instance instance) {
        if (RedisMode.STANDALONE.equals(redisMode) && !RedisReplicationRole.MASTER.equals(instance.getRole())) {
            String[] masterIpAndPort = instance.getSlaveof().split(":");
            return String.format("'%s %s'", masterIpAndPort[0], masterIpAndPort[1]);
        } else {
            return Constant.Strings.EMPTY_PASSWORD;
        }
    }

    @Override
    public List<ClusterBO> queryAllRedisClusters() {
        List<ClusterBO> redisClusterBOs = new ArrayList<>();
        List<Cluster> clusters = clusterEntityService.list();
        clusters.forEach(each -> {
            redisClusterBOs.add(createRedisClusterBO(each,
                    instanceProcessService.queryInstanceBOsByClusterId(each.getClusterId())));
        });
        return redisClusterBOs;
    }

    @Override
    public List<Cluster> queryByUserName(String userName) {
        if (userRoleProcessService.isAdmin(userName)) {
            return clusterEntityService.list();
        } else {
            List<Cluster> clusters = clusterEntityService.queryByUserName(userName);
            clusters.addAll(queryHasPermissionClusters(userName));
            return clusters;
        }
    }

    private List<Cluster> queryHasPermissionClusters(final String userName) {
        List<Cluster> clusters = new ArrayList<>();
        List<ClusterPermission> clusterPermissions = clusterPermissionEntityService.queryByUserName(userName);
        clusterPermissions.forEach(each -> {
            clusters.add(clusterEntityService.getByClusterId(each.getClusterId()));
        });
        return clusters;
    }

    @Override
    public ClusterBO getRedisClusterById(final String clusterId) {
        Cluster cluster = clusterEntityService.getByClusterId(clusterId);
        Preconditions.checkNotNull(cluster, String.format("Can find a redis cluster by cluster id '%s'.", clusterId));
        return createRedisClusterBO(cluster, instanceProcessService.queryInstanceBOsByClusterId(clusterId));
    }
}
