package com.dangdang.cymbal.service.operation.service.utility.impl;

import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisOperationUtilityService;
import com.dangdang.cymbal.service.util.RedisUtil;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Implement of {@link RedisOperationUtilityService}.
 *
 * @auther GeZhen
 */
@Service
public class RedisOperationUtilityServiceImpl implements RedisOperationUtilityService {

    @Resource
    private InstanceEntityService instanceEntityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @Override
    @Transactional
    public void startup(final Set<Integer> redisServerInstanceIds) {
        redisServerInstanceIds.forEach(each -> {
            InstanceBO instanceBO = instanceProcessService.getInstanceBOById(each);
            Preconditions.checkNotNull(instanceBO);
            startup(instanceBO);
        });
    }

    @Override
    @Transactional
    public void startup(final List<InstanceBO> instanceBOs) {
        instanceBOs.forEach(each -> {
            startup(each);
        });
    }

    @Override
    @Transactional
    public void startup(final InstanceBO instanceBO) {
        redisClientUtilityService.startup(instanceBO);
        updateInstanceStatus(instanceBO.getSelf(), InstanceStatus.STARTED);
    }

    @Override
    @Transactional
    public void shutdown(final List<Integer> instanceIds) {
        instanceIds.forEach(each -> {
            InstanceBO instanceBO = instanceProcessService.getInstanceBOById(each);
            Preconditions.checkNotNull(instanceBO);
            shutdown(instanceBO);
        });
    }

    @Override
    @Transactional
    public void shutdown(final InstanceBO instanceBO) {
        redisClientUtilityService.shutdown(instanceBO);
        updateInstanceStatus(instanceBO.getSelf(), InstanceStatus.STOPPED);
    }

    private void updateInstanceStatus(final Instance instance, final InstanceStatus instanceStatus) {
        instance.setStatus(instanceStatus);
        instanceEntityService.updateById(instance);
    }

    @Override
    @Transactional
    public void slaveOf(final Integer instanceId, final String newMasterHost, final Integer newMasterPort,
            String newMasterPassword) {
        InstanceBO instanceBO = instanceProcessService.getInstanceBOById(instanceId);
        this.slaveOf(instanceBO, newMasterHost, newMasterPort, newMasterPassword);
    }

    @Override
    @Transactional
    public void slaveOf(final InstanceBO instanceBO, final String newMasterHost, final Integer newMasterPort,
            final String newMasterPassword) {
        redisClientUtilityService.slaveOf(instanceBO, newMasterHost, newMasterPort, newMasterPassword);
        this.updateInstanceRoleAndSlaveOf(instanceBO.getSelf(), RedisReplicationRole.SLAVE,
                RedisUtil.getSlaveOf(newMasterHost, newMasterPort));
    }

    @Override
    @Transactional
    public void failover(final Integer instanceId) {
        InstanceBO instanceBO = instanceProcessService.getInstanceBOById(instanceId);
        this.failover(instanceBO);
    }

    @Override
    @Transactional
    public void failover(final InstanceBO instanceBO) {
        redisClientUtilityService.failover(instanceBO);
        exchangeRole(instanceBO);
    }

    private void exchangeRole(final InstanceBO instanceBO) {
        Instance oldMaster = instanceBO.getMaster().getSelf();
        Instance oldSlave = instanceBO.getSelf();
        updateInstanceRoleAndSlaveOf(oldMaster, RedisReplicationRole.SLAVE,
                RedisUtil.getSlaveOf(instanceBO.getNode().getIp(), oldSlave.getPort()));
        updateInstanceRoleAndSlaveOf(oldSlave, RedisReplicationRole.MASTER, null);
    }

    private void updateInstanceRoleAndSlaveOf(final Instance instance, final RedisReplicationRole role,
            final String salveOf) {
        instance.setRole(role);
        instance.setSlaveof(salveOf);
        instanceEntityService.updateById(instance);
    }
}
