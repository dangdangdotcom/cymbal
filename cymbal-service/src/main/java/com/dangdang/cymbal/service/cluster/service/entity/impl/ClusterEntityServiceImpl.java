package com.dangdang.cymbal.service.cluster.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.service.cluster.mapper.ClusterMapper;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implement of {@link ClusterEntityService}.
 *
 * @auther GeZhen
 */
@Service
public class ClusterEntityServiceImpl extends ServiceImpl<ClusterMapper, Cluster> implements ClusterEntityService {

    @Override
    public Cluster getByClusterId(final String clusterId) {
        Preconditions.checkNotNull(clusterId);
        return this.lambdaQuery().eq(Cluster::getClusterId, clusterId).one();
    }

    @Override
    public List<Cluster> queryByUserName(final String userName) {
        Preconditions.checkNotNull(userName);
        return this.lambdaQuery().eq(Cluster::getUserName, userName).list();
    }
}
