package com.dangdang.cymbal.service.cluster.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.Cluster;

import java.util.List;

/**
 * Entity service for RedisClusterCreatedEvent.
 *
 * @auther GeZhen
 */
public interface ClusterEntityService extends IService<Cluster> {

    /**
     * Get and return a redis cluster by giving cluster id.
     *
     * @param clusterId cluster id
     * @return redis cluster with giving cluster id
     */
    Cluster getByClusterId(String clusterId);

    /**
     * Get and return redis clusters by giving user name.
     *
     * @param userName user name
     * @return redis clusters with giving user name
     */
    List<Cluster> queryByUserName(String userName);
}
