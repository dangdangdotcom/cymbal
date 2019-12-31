package com.dangdang.cymbal.service.node.service.process;

import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;

import java.util.List;
import java.util.Set;

/**
 * Process service for node.
 *
 * @auther GeZhen
 */
public interface NodeProcessService {

    /**
     * create nodes from a excel file.
     *
     * @param excelFilePath excel file path
     * @return size of added nodes
     */
    int createNodesFromExcel(String excelFilePath);

    /**
     * Save a node entity, and init it.
     *
     * @param node node to save
     */
    void saveAndInitNode(Node node);

    /**
     * Init target nodes.
     * Do some prepare work for node, such like install redis, redis-exporter and so on.
     *
     * @param nodes nodes to init
     * @return nodes which init failed
     */
    List<Node> initNodes(List<Node> nodes);

    /**
     * Init nodes of given ids.
     *
     * @param nodeIds node ids
     * @return nodes which init failed
     */
    List<Node> initNodes(Set<Integer> nodeIds);

    /**
     * Query available nodes for target resources.
     * Available nodes means:
     * 1. Sum of free resources on existed nodes muster larger than need.
     *    eg: free memory must larger than masterCount * (replicaCount + 1) * cacheSize.
     * 2. If master count is larger than 3, then count of nodes with enough resources must larger than 3 too.
     * 3. Each node must have resources for at least one instance.
     *
     * @param cacheSize    cache size per redis server instance
     * @param masterCount  master count in cluster
     * @param replicaCount replica count of each master
     * @return available nodes
     */
    List<Node> queryAvailableNodes(InternetDataCenter idc, int cacheSize, int masterCount, int replicaCount);

    /**
     * Update node info.
     *
     * @param node node with new info
     */
    void updateNode(Node node);
}
