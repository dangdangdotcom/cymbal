package com.dangdang.cymbal.service.cluster.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.Instance;

import java.util.List;

/**
 * Entity service for Instance.
 *
 * @auther GeZhen
 */
public interface InstanceEntityService extends IService<Instance> {

    /**
     * Query max port of redis server instance in target node.
     *
     * @param nodeId node id
     * @return max port of node
     */
    int queryMaxUsedPortOfNode(Integer nodeId);

    /**
     * Query redis server instances by given cluster id.
     *
     * @param clusterId cluster id
     * @return redis server instance of target cluster
     */
    List<Instance> queryByClusterId(String clusterId);

    /**
     * Query redis server instances by given cluster id.
     *
     * @param nodeId node id
     * @return redis server instance of target cluster
     */
    List<Instance> queryByNodeId(Integer nodeId);

    /**
     * Get a redis server instance by ip and port.
     *
     * @param nodeId node id
     * @param port port
     * @return instance
     */
    Instance getByNodeIdAndPort(Integer nodeId, Integer port);
}
