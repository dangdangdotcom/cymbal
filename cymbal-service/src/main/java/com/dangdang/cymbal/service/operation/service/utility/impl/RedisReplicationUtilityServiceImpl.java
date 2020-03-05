package com.dangdang.cymbal.service.operation.service.utility.impl;

import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.operation.service.utility.RedisReplicationUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.enums.ShellCommand;
import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @auther GeZhen
 */
@Service
public class RedisReplicationUtilityServiceImpl implements RedisReplicationUtilityService {

    @Resource
    private RedisShellUtilityService redisShellUtilityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Resource
    private InstanceEntityService instanceEntityService;

    @Override
    public boolean refreshReplication(final List<InstanceBO> instanceBOs) {
        boolean changed = false;
        for (InstanceBO each : instanceBOs) {
            if (refreshReplication(each)) {
                changed = true;
            }
        }
        return changed;
    }

    private boolean refreshReplication(final InstanceBO instanceBO) {
        boolean changed = false;
        try {
            if (InstanceType.SENTINEL.equals(instanceBO.getSelf().getType())) {
                changed = refreshReplicationForSentinel(instanceBO);
            } else {
                changed = refreshReplicationForRedis(instanceBO);
            }
        } catch (ShellExecutionException | IllegalStateException e) {
            instanceBO.getSelf().setStatus(InstanceStatus.STOPPED);
            changed = true;
        } finally {
            if (changed) {
                instanceEntityService.updateById(instanceBO.getSelf());
            }
            return changed;
        }
    }

    private boolean refreshReplicationForSentinel(final InstanceBO instanceBO) {
        List<String> queryResult = redisShellUtilityService
                .executeSentinelShellScript(instanceBO, ShellCommand.CHECK, instanceBO.getNode().getIp(),
                        instanceBO.getSelf().getPort().toString());
        Preconditions.checkState(!queryResult.isEmpty());
        return changStatusIfNeeded(instanceBO.getSelf());
    }

    private boolean refreshReplicationForRedis(final InstanceBO instanceBO) {
        List<String> queryResult = redisShellUtilityService
                .executeRedisShellScript(instanceBO, ShellCommand.CHECK, instanceBO.getNode().getIp(),
                        instanceBO.getSelf().getPort().toString());
        Preconditions.checkState(!queryResult.isEmpty());
        return changeReplicationAndStatusIfNeeded(instanceBO.getSelf(), queryResult);
    }

    private boolean changeReplicationAndStatusIfNeeded(final Instance instance, final List<String> queryResult) {
        boolean changed = changStatusIfNeeded(instance);
        RedisReplicationRole actualRole = RedisReplicationRole.valueOf(queryResult.get(0).toUpperCase());
        switch (actualRole) {
            case MASTER:
                if (!actualRole.equals(instance.getRole())) {
                    instance.setRole(actualRole);
                    instance.setSlaveof(null);
                    changed = true;
                }
                break;
            case SLAVE:
                if (!actualRole.equals(instance.getRole())) {
                    instance.setRole(actualRole);
                    changed = true;
                }
                String actualSlaveof = String.format("%s:%s", queryResult.get(1), queryResult.get(2));
                if (!actualSlaveof.equals(instance.getSlaveof())) {
                    instance.setSlaveof(actualSlaveof);
                    changed = true;
                }
                break;
            default:
                break;
        }
        return changed;
    }

    private boolean changStatusIfNeeded(final Instance instance) {
        if (InstanceStatus.STOPPED.equals(instance.getStatus())) {
            instance.setStatus(InstanceStatus.STARTED);
            return true;
        }
        return false;
    }

    @Override
    public boolean refreshReplication(String clusterId) {
        return this.refreshReplication(instanceProcessService.queryInstanceBOsByClusterId(clusterId));
    }
}
