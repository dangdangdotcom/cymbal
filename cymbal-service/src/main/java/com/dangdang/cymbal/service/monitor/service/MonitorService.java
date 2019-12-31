package com.dangdang.cymbal.service.monitor.service;

import com.dangdang.cymbal.service.monitor.bean.RedisMonitorInfo;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Node;

import java.util.List;

/**
 * Monitor for node or redis server instance.
 *
 * @auther GeZhen
 */
public interface MonitorService {

    /**
     * Init monitor on nodes.
     *
     * @param nodes nodes to init monitor
     */
    void initMonitorForNodes(List<Node> nodes);

    /**
     * Init monitor for redis cluster.
     *
     * @param redisClusterBO redis cluster BO
     */
    void initMonitorForCluster(ClusterBO redisClusterBO);

    /**
     * Init monitor for redis cluster.
     *
     * @param clusterId redis cluster id
     */
    void initMonitorForClusterAndNodes(String clusterId);

    /**
     * Init monitor for redis server instances.
     *
     * @param instanceBOs instance BOs
     */
    void initMonitorForInstances(List<InstanceBO> instanceBOs);

    /**
     * Pull runtime monitor info of instance.
     *
     * @param instanceId instance id
     * @return runtime monitor info
     */
    RedisMonitorInfo scrapeMonitorInfo(Integer instanceId);
}
