package com.dangdang.cymbal.service.auth.service.process;

import com.dangdang.cymbal.domain.po.ClusterPermission;

/**
 * Process service for {@link ClusterPermission}.
 *
 * @auther GeZhen
 */
public interface ClusterPermissionProcessService {

    /**
     * Check user has permission of cluster.
     *
     * @param clusterId cluster id
     * @param userName user name
     * @return true if has permission
     */
    boolean hasPermissionOfCluster(String clusterId, String userName);
}
