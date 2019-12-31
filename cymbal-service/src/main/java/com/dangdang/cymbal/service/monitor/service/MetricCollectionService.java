package com.dangdang.cymbal.service.monitor.service;


import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Node;

import java.util.List;

/**
 * Metric collection service.
 *
 * @author GeZhen
 */
public interface MetricCollectionService {

    /**
     * Add a node to metric collection system.
     *
     * @param node node to add
     */
    void addNode(Node node);

    /**
     * Add a list of nodes to metric collection system.
     *
     * @param nodes nodes to add
     */
    void addNodes(List<Node> nodes);

    /**
     * Add a redis server instance to metric collection system.
     *
     * @param host host
     * @param port port
     * @param clusterId cluster id
     * @param password password
     */
    void addInstance(String host, int port, String clusterId, String password);

    /**
     * Add a list of redis server instances to metric collection system.
     *
     * @param instanceBOs instance BOs
     */
    void addInstances(List<InstanceBO> instanceBOs);
}
