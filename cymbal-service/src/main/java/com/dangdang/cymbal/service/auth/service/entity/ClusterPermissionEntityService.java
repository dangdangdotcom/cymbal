package com.dangdang.cymbal.service.auth.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.ClusterPermission;

import java.util.List;

/**
 * Entity service for redis cluster permission.
 *
 * @auther GeZhen
 */
public interface ClusterPermissionEntityService extends IService<ClusterPermission> {

    /**
     * Get redis cluster permissions of a given cluster id.
     *
     * @param clusterId cluster id
     * @return redis cluster permissions
     */
    List<ClusterPermission> queryByClusterId(String clusterId);

    /**
     * Get redis cluster permissions of a given user name.
     *
     * @param userName user name
     * @return redis cluster permissions
     */
    List<ClusterPermission> queryByUserName(String userName);
}
