package com.dangdang.cymbal.service.auth.service.process.impl;

import com.dangdang.cymbal.domain.po.ClusterPermission;
import com.dangdang.cymbal.service.auth.service.entity.ClusterPermissionEntityService;
import com.dangdang.cymbal.service.auth.service.process.ClusterPermissionProcessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Implement of {@link ClusterPermissionProcessService}.
 *
 * @auther GeZhen
 */
@Service
public class ClusterPermissionProcessServiceImpl implements ClusterPermissionProcessService {

    @Resource
    private ClusterPermissionEntityService clusterPermissionEntityService;

    @Override
    public boolean hasPermissionOfCluster(final String clusterId, final String userName) {
        return clusterPermissionEntityService.lambdaQuery().eq(ClusterPermission::getClusterId, clusterId)
                .eq(ClusterPermission::getUserName, userName).count() > 0;
    }
}
