package com.dangdang.cymbal.service.cluster.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.service.cluster.mapper.InstanceMapper;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implement of InstanceEntityService.
 *
 * @auther GeZhen
 */
@Service
public class InstanceEntityServiceImpl extends ServiceImpl<InstanceMapper, Instance> implements InstanceEntityService {

    @Override
    public int queryMaxUsedPortOfNode(final Integer nodeId) {
        return baseMapper.queryMaxUsedPortOfNode(nodeId);
    }

    @Override
    public List<Instance> queryByClusterId(final String clusterId) {
        return this.lambdaQuery().eq(Instance::getClusterId, clusterId).list();
    }

    @Override
    public List<Instance> queryByNodeId(final Integer nodeId) {
        return this.lambdaQuery().eq(Instance::getNodeId, nodeId).list();
    }

    @Override
    public Instance getByNodeIdAndPort(final Integer nodeId, final Integer port) {
        return this.lambdaQuery().eq(Instance::getNodeId, nodeId).eq(Instance::getPort, port).one();
    }

}
