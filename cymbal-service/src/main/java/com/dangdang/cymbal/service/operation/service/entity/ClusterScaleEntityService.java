package com.dangdang.cymbal.service.operation.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.ClusterScale;

import java.util.List;

/**
 * Entity service for {@link ClusterScale}.
 *
 * @auther GeZhen
 */
public interface ClusterScaleEntityService extends IService<ClusterScale> {

    /**
     * Query redis cluster scales by cluster id.
     *
     * @param clusterId cluster id
     * @return redis cluster scales
     */
    List<ClusterScale> queryByClusterId(String clusterId);
}
