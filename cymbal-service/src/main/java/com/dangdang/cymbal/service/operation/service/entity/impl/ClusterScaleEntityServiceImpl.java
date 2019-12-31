package com.dangdang.cymbal.service.operation.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.ClusterScale;
import com.dangdang.cymbal.service.operation.mapper.ClusterScaleMapper;
import com.dangdang.cymbal.service.operation.service.entity.ClusterScaleEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implement for {@link ClusterScaleEntityService}.
 *
 * @auther GeZhen
 */
@Service
public class ClusterScaleEntityServiceImpl extends ServiceImpl<ClusterScaleMapper, ClusterScale>
        implements ClusterScaleEntityService {

    @Override
    public List<ClusterScale> queryByClusterId(final String clusterId) {
        return this.lambdaQuery().eq(ClusterScale::getClusterId, clusterId)
                .orderByDesc(ClusterScale::getLastChangedDate).list();
    }
}
