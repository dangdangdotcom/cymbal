package com.dangdang.cymbal.web.security.checker;

import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.service.auth.service.process.ClusterPermissionProcessService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Check permission for cluster.
 *
 * @auther Zhen.Ge
 */
@Component
public class ClusterPermissionChecker {

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private InstanceEntityService instanceEntityService;

    @Resource
    private ClusterPermissionProcessService redisClusterPermissionProcessService;

    public boolean hasViewPermissionForInstance(final Integer instanceId, final String userName) {
        Instance instance = instanceEntityService.getById(instanceId);
        return hasViewPermissionForCluster(instance.getClusterId(), userName);
    }

    public boolean hasViewPermissionForCluster(final String clusterId, final String userName) {
        if (hasOperationPermissionForCluster(clusterId, userName)) {
            return true;
        } else {
            return redisClusterPermissionProcessService.hasPermissionOfCluster(clusterId, userName);
        }
    }

    public boolean hasOperationPermissionForCluster(final String clusterId, final String userName) {
        if (userRoleProcessService.isAdmin(userName)) {
            return true;
        }
        if (clusterEntityService.getByClusterId(clusterId).getUserName().endsWith(userName)) {
            return true;
        }
        return false;
    }

    public boolean hasOperationPermissionForInstances(final List<Integer> instanceIds, final String userName) {
        if (userRoleProcessService.isAdmin(userName)) {
            return true;
        }
        for (Integer each : instanceIds) {
            Instance instance = instanceEntityService.getById(each);
            if (!this.hasOperationPermissionForCluster(instance.getClusterId(), userName)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasOperationPermissionForInstance(final Integer instanceId, final String userName) {
        return this.hasOperationPermissionForInstances(Arrays.asList(instanceId), userName);
    }
}
