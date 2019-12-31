package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.dto.ClusterDTO;
import com.dangdang.cymbal.web.object.dto.ImportClusterDTO;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.AlarmLevel;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converter for redis cluster.
 *
 * @auther GeZhen
 */
@Component
public class ClusterConverter extends BaseConverter<Cluster, ClusterDTO> {

    @Override
    void poToDto(Cluster cluster, ClusterDTO clusterDTO) {

    }

    @Override
    void dtoToPo(ClusterDTO clusterDTO, Cluster cluster) {

    }

    /**
     * Convert DTO to BO when importing redis cluster.
     *
     * @param importClusterDTO import redis cluster dto
     * @return redis cluster BO
     */
    public ClusterBO dtoToBo(final ImportClusterDTO importClusterDTO) {
        Cluster cluster = createRedisCluster(importClusterDTO);
        return ClusterBO.builder().cluster(cluster)
                .instanceBOs(createRedisServerInstanceBOs(importClusterDTO, cluster.getClusterId())).build();
    }

    private Cluster createRedisCluster(final ImportClusterDTO importClusterDTO) {
        Cluster cluster = new Cluster();
        BeanUtils.copyProperties(importClusterDTO, cluster);
        cluster.setClusterId(RedisUtil.generateClusterId());
        cluster.setStatus(ClusterStatus.UP);
        cluster.setAlarmLevel(AlarmLevel.ALARM);
        // TODO: Enable import sentinel instance.
        cluster.setEnableSentinel(false);
        return cluster;
    }

    private List<InstanceBO> createRedisServerInstanceBOs(final ImportClusterDTO importClusterDTO,
            final String clusterId) {
        List<InstanceBO> instanceBOs = new ArrayList();
        Map<String, Node> ipNodeMap = new HashMap();
        for (String uri : importClusterDTO.getRedisInstanceURIs()) {
            String[] ipAndPort = uri.split(":");
            Instance instance = createRedisServerInstance(Integer.valueOf(ipAndPort[1]), clusterId,
                    importClusterDTO.getRedisVersion(), InstanceType.REDIS);
            Node node = ipNodeMap.computeIfAbsent(ipAndPort[0], ip -> createNode(ip, importClusterDTO));
            instanceBOs.add(InstanceBO.builder().node(node).self(instance).password(importClusterDTO.getPassword())
                    .build());
        }
        return instanceBOs;
    }

    private Instance createRedisServerInstance(final Integer port, final String clusterId, final String version,
            final InstanceType type) {
        Instance instance = new Instance();
        instance.setPort(port);
        instance.setRedisVersion(version);
        instance.setClusterId(clusterId);
        instance.setStatus(InstanceStatus.STARTED);
        instance.setRole(RedisReplicationRole.MASTER);
        instance.setType(type);
        return instance;
    }

    private Node createNode(String ip, final ImportClusterDTO importClusterDTO) {
        Node node = new Node();
        node.setIp(ip);
        node.setHost(ip);
        node.setHost(importClusterDTO.getDescription());
        node.setPassword(importClusterDTO.getNodePassword());
        node.setStatus(NodeStatus.UNINITIALIZED);
        node.setFreeMemory(0);
        node.setTotalMemory(0);
        node.setEnv(importClusterDTO.getEnv());
        node.setIdc(importClusterDTO.getIdc());
        node.setCreationDate(new Date());
        node.setLastChangedDate(node.getCreationDate());
        return node;
    }
}
