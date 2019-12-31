package com.dangdang.cymbal.service.cluster.service.process.impl;

import com.dangdang.cymbal.common.util.CollectionUtil;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.node.service.process.NodeProcessService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisReplicationUtilityService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implement of {@link InstanceProcessService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class InstanceProcessServiceImpl implements InstanceProcessService {

    private final static int DEFAULT_REDIS_SERVER_PORT = 8381;

    private final static int DEFAULT_SENTINEL_SERVER_PORT_PLUS = 1000;

    @Resource
    private InstanceEntityService instanceEntityService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @Resource
    private NodeEntityService nodeEntityService;

    @Resource
    private NodeProcessService nodeProcessService;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private RedisReplicationUtilityService redisReplicationUtilityService;

    @Override
    public List<InstanceBO> createInstances(final Cluster cluster) {
        List<Node> nodesToUse = queryAvailableNodes(cluster);
        return createRedisServerInstanceOnNodes(cluster, nodesToUse);
    }

    private List<Node> queryAvailableNodes(final Cluster cluster) {
        List<Node> nodesToUse = nodeProcessService
                .queryAvailableNodes(cluster.getIdc(), cluster.getCacheSize(), cluster.getMasterCount(),
                        cluster.getReplicaCount());
        return nodesToUse;
    }

    private List<InstanceBO> createRedisServerInstanceOnNodes(final Cluster cluster, final List<Node> nodes) {
        List<InstanceBO> instanceBOS = createBaseRedisServerInstances(cluster, nodes);
        assignReplica(cluster, instanceBOS);
        createSentinelServerInstanceIfNeeded(cluster, instanceBOS);
        instanceEntityService.saveBatch(instanceBOS.stream().map(InstanceBO::getSelf).collect(Collectors.toList()));
        nodeEntityService.updateBatchById(nodes);
        return instanceBOS;
    }

    private List<InstanceBO> createBaseRedisServerInstances(final Cluster cluster, final List<Node> nodes) {
        int instanceCount = cluster.getMasterCount() * (cluster.getReplicaCount() + 1);
        List<InstanceBO> instanceBOS = new ArrayList<>(instanceCount);
        int port = getAvailablePort(nodes);
        int nodeIndex = 0;
        List<Node> sortedNodes = nodes.stream().sorted(Comparator.comparing(Node::getFreeMemory).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < instanceCount; i++) {
            Node node = sortedNodes.get(nodeIndex++);
            if (node.getFreeMemory() >= cluster.getCacheSize()) {
                RedisReplicationRole role = RedisReplicationRole.MASTER;
                if (i >= cluster.getMasterCount()) {
                    role = RedisReplicationRole.SLAVE;
                }

                instanceBOS.add(createInstanceBO(cluster, node, port, role, InstanceType.REDIS));
                node.setFreeMemory(node.getFreeMemory() - cluster.getCacheSize());
            }
            if (node.getFreeMemory() < cluster.getCacheSize()) {
                sortedNodes.remove(node);
            }
            if (nodeIndex >= sortedNodes.size()) {
                port++;
                nodeIndex = 0;
            }
        }
        return instanceBOS;
    }

    private int getAvailablePort(final List<Node> nodes) {
        int maxPort = nodes.stream().mapToInt(each -> instanceEntityService.queryMaxUsedPortOfNode(each.getId())).max()
                .getAsInt() + 1;
        return maxPort > DEFAULT_REDIS_SERVER_PORT ? maxPort : DEFAULT_REDIS_SERVER_PORT;
    }

    private InstanceBO createInstanceBO(final Cluster cluster, final Node node, final int port,
            final RedisReplicationRole role, final InstanceType type) {
        Instance instance = createInstanceEntity(cluster, node, port, role, type);
        return createInstanceBO(instance, cluster, node);
    }

    private InstanceBO createInstanceBO(final Instance instance, final Cluster cluster, final Node node) {
        return InstanceBO.builder()
                .self(instance)
                .password(cluster.getPassword())
                .cacheSize(cluster.getCacheSize())
                .node(node)
                .build();
    }

    private Instance createInstanceEntity(final Cluster cluster, final Node node, final int port,
            final RedisReplicationRole role, final InstanceType type) {
        Instance instance = new Instance();
        instance.setClusterId(cluster.getClusterId());
        instance.setStatus(InstanceStatus.STARTED);
        instance.setRole(role);
        instance.setNodeId(node.getId());
        instance.setPort(port);
        instance.setRedisVersion(cluster.getRedisVersion());
        instance.setType(type);
        instance.setCreationDate(new Date());
        instance.setLastChangedDate(instance.getCreationDate());
        return instance;
    }

    private void assignReplica(final Cluster cluster, final List<InstanceBO> instanceBOS) {
        for (int i = cluster.getMasterCount(); i < instanceBOS.size(); i += cluster.getMasterCount()) {
            for (int j = i; j < i + cluster.getMasterCount(); j++) {
                int masterIndex = (j + 1) % cluster.getMasterCount();
                instanceBOS.get(j).setMaster(instanceBOS.get(masterIndex));
                instanceBOS.get(j).getSelf().setSlaveof(
                        String.format("%s:%d", instanceBOS.get(masterIndex).getNode().getIp(),
                                instanceBOS.get(masterIndex).getSelf().getPort().intValue()));
            }
        }
    }

    private void createSentinelServerInstanceIfNeeded(final Cluster cluster, final List<InstanceBO> mastersAndSlaves) {
        if (cluster.getRedisMode().equals(RedisMode.STANDALONE) && cluster.isEnableSentinel()) {
            List<InstanceBO> sentinels = new ArrayList<>(mastersAndSlaves.size());
            mastersAndSlaves.forEach(each -> {
                int sentinelPort = each.getSelf().getPort() + DEFAULT_SENTINEL_SERVER_PORT_PLUS;
                InstanceBO sentinelServerInstanceBO = createInstanceBO(cluster, each.getNode(), sentinelPort,
                        RedisReplicationRole.MASTER, InstanceType.SENTINEL);
                sentinelServerInstanceBO.setMaster(each.getMaster() == null ? each : each.getMaster());
                sentinels.add(sentinelServerInstanceBO);
            });
            mastersAndSlaves.addAll(sentinels);
        }
    }

    @Override
    @Transactional
    public void queryAndUpdateRedisClusterNodeId(final ClusterBO redisClusterBO) {
        Preconditions.checkNotNull(redisClusterBO);
        Preconditions.checkArgument(!redisClusterBO.getInstanceBOs().isEmpty());
        InstanceBO oneInstanceBO = CollectionUtil.getFirst(redisClusterBO.getInstanceBOs());
        List<ClusterNodeBO> clusterNodeBOs = redisClientUtilityService.clusterNodes(oneInstanceBO);
        updateRedisClusterNodeId(redisClusterBO.getInstanceBOs(), clusterNodeBOs);
    }

    private void updateRedisClusterNodeId(final List<InstanceBO> instanceBOS,
            final List<ClusterNodeBO> clusterNodeBOs) {
        instanceBOS.forEach(each -> {
            for (ClusterNodeBO clusterNodeBO : clusterNodeBOs) {
                if (clusterNodeBO.getIp().equals(each.getNode().getIp()) && clusterNodeBO.getPort()
                        .equals(each.getSelf().getPort())) {
                    each.getSelf().setClusterNodeId(clusterNodeBO.getClusterNodeId());
                    break;
                }
            }
        });
        instanceEntityService
                .updateBatchById(instanceBOS.stream().map(InstanceBO::getSelf).collect(Collectors.toList()));
    }

    @Override
    public void importInstance(final ClusterBO redisClusterBO) {
        saveAndInitNodesOfRedisServerInstanceBOs(redisClusterBO.getInstanceBOs());
        supplementRedisServerInstances(redisClusterBO);
        redisReplicationUtilityService.refreshReplication(redisClusterBO.getInstanceBOs());
        saveRedisServerInstances(redisClusterBO);
    }

    private void supplementRedisServerInstances(final ClusterBO redisClusterBO) {
        Cluster cluster = redisClusterBO.getCluster();
        if (RedisMode.CLUSTER.equals(cluster.getRedisMode())) {
            InstanceBO oneInstanceBO = CollectionUtil.getFirst(redisClusterBO.getInstanceBOs());
            List<ClusterNodeBO> clusterNodeBOs = redisClientUtilityService.clusterNodes(oneInstanceBO);
            if (clusterNodeBOs.size() > redisClusterBO.getInstanceBOs().size()) {
                List<InstanceBO> otherInstanceBOS = createOtherRedisServerInstanceBOsByClusterNodeBOs(redisClusterBO,
                        clusterNodeBOs);
                saveAndInitNodesOfRedisServerInstanceBOs(otherInstanceBOS);
                redisClusterBO.getInstanceBOs().addAll(otherInstanceBOS);
            }
            updateRedisClusterNodeId(redisClusterBO.getInstanceBOs(), clusterNodeBOs);
        }
    }

    private List<InstanceBO> createOtherRedisServerInstanceBOsByClusterNodeBOs(final ClusterBO redisClusterBO,
            final List<ClusterNodeBO> clusterNodeBOs) {
        List<InstanceBO> otherInstanceBOS = new ArrayList<>();
        Map<String, Node> ipNodeMap = new HashMap();
        // TODO 优化
        clusterNodeBOs.forEach(eachClusterNodeBO -> {
            boolean unknownInstance = true;
            Node node = null;
            for (InstanceBO each : redisClusterBO.getInstanceBOs()) {
                if (each.getNode().getIp().equals(eachClusterNodeBO.getIp())) {
                    node = each.getNode();
                    if (each.getSelf().getPort().equals(eachClusterNodeBO.getPort())) {
                        unknownInstance = false;
                        break;
                    }
                }
            }
            if (unknownInstance) {
                if (Objects.isNull(node)) {
                    Node sourceNode = redisClusterBO.getInstanceBOs().get(0).getNode();
                    node = ipNodeMap.computeIfAbsent(eachClusterNodeBO.getIp(),
                            ip -> createNode(sourceNode.getIdc(), sourceNode.getEnv(), sourceNode.getHost(),
                                    eachClusterNodeBO.getIp(), sourceNode.getPassword()));
                }
                otherInstanceBOS.add(createInstanceBO(redisClusterBO.getCluster(), node, eachClusterNodeBO.getPort(),
                        eachClusterNodeBO.getRole(), InstanceType.REDIS));
            }
        });
        return otherInstanceBOS;
    }

    private Node createNode(final InternetDataCenter internetDataCenter, final Environment environment,
            final String host, final String ip, final String password) {
        Node node = new Node();
        node.setIdc(internetDataCenter);
        node.setEnv(environment);
        node.setHost(host);
        node.setIp(ip);
        node.setPassword(password);
        node.setStatus(NodeStatus.UNINITIALIZED);
        return node;
    }

    private void saveAndInitNodesOfRedisServerInstanceBOs(final List<InstanceBO> instanceBOS) {
        instanceBOS.stream().map(InstanceBO::getNode).distinct().forEach(each -> {
            nodeProcessService.saveAndInitNode(each);
        });
    }

    private void saveRedisServerInstances(final ClusterBO redisClusterBO) {
        redisClusterBO.getInstanceBOs().forEach(each -> {
            each.getSelf().setNodeId(each.getNode().getId());
            each.getSelf().setClusterId(redisClusterBO.getCluster().getClusterId());
            instanceEntityService.save(each.getSelf());
        });
    }

    @Override
    public List<InstanceBO> queryInstanceBOsByClusterId(final String clusterId) {
        List<Instance> instances = instanceEntityService.queryByClusterId(clusterId);
        return convertToBOs(instances);
    }

    private List<InstanceBO> convertToBOs(final List<Instance> instances) {
        return instances.stream().map(this::convertToBO).collect(Collectors.toList());
    }

    private InstanceBO convertToBO(final Instance instance) {
        Cluster cluster = clusterEntityService.getByClusterId(instance.getClusterId());
        Node node = nodeEntityService.getById(instance.getNodeId());
        InstanceBO instanceBO = createInstanceBO(instance, cluster, node);
        this.setMasterIfNeeded(instanceBO);
        return instanceBO;
    }

    private void setMasterIfNeeded(final InstanceBO instanceBO) {
        if (instanceBO.getSelf().getRole().equals(RedisReplicationRole.SLAVE)) {
            instanceBO.setMaster(this.getMasterBO(instanceBO.getSelf().getSlaveof()));
        }
    }

    private InstanceBO getMasterBO(final String endPoint) {
        Preconditions.checkNotNull(endPoint);
        String[] ipAndPort = endPoint.split(":");
        Preconditions.checkState(ipAndPort.length == 2);
        Node masterNode = nodeEntityService.getByIp(ipAndPort[0]);
        Instance master = instanceEntityService.getByNodeIdAndPort(masterNode.getId(), Integer.valueOf(ipAndPort[1]));
        return convertToBO(master);
    }

    @Override
    public List<InstanceBO> queryInstanceBOsByNodeId(final Integer nodeId) {
        List<Instance> instances = instanceEntityService.queryByNodeId(nodeId);
        return convertToBOs(instances);
    }

    @Override
    public InstanceBO getInstanceBOById(final Integer instanceId) {
        return convertToBO(instanceEntityService.getById(instanceId));
    }
}
