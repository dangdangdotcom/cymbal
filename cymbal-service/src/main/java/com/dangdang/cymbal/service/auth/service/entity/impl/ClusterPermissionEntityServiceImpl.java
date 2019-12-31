package com.dangdang.cymbal.service.auth.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.ClusterPermission;
import com.dangdang.cymbal.service.auth.mapper.ClusterPermissionMapper;
import com.dangdang.cymbal.service.auth.service.entity.ClusterPermissionEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implement of ClusterPermissionEntityService.
 *
 * @auther GeZhen
 */
@Service
public class ClusterPermissionEntityServiceImpl extends ServiceImpl<ClusterPermissionMapper, ClusterPermission>
        implements ClusterPermissionEntityService {

    @Override
    public List<ClusterPermission> queryByClusterId(final String clusterId) {
        return this.lambdaQuery().eq(ClusterPermission::getClusterId, clusterId).list();
    }

    @Override
    public List<ClusterPermission> queryByUserName(final String userName) {
        return this.lambdaQuery().eq(ClusterPermission::getUserName, userName).list();
    }
}
