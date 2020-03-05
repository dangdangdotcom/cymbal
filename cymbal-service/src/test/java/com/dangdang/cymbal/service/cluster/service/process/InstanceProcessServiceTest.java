package com.dangdang.cymbal.service.cluster.service.process;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.dangdang.cymbal.service.util.service.SshClientService;
import com.google.common.base.Strings;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InstanceProcessServiceTest {

    @Resource
    InstanceProcessService instanceProcessService;

    @Resource
    InstanceEntityService instanceEntityService;

    @Resource
    NodeEntityService nodeEntityService;

    @Resource
    ClusterEntityService clusterEntityService;

    @MockBean
    RedisClientUtilityService redisClientUtilityService;

    @MockBean
    RedisShellUtilityService redisShellUtilityService;

    @MockBean
    SshClientService sshClientService;

    @MockBean
    AnsibleService ansibleService;

    @Test
    @Transactional
    public void createInstances() {
        List<Node> nodes = buildNodes();
        int originFreeMemoryCount = nodes.stream().mapToInt(Node::getFreeMemory).sum();
        Cluster cluster = buildRedisCluster();
        List<InstanceBO> instanceBOs = instanceProcessService.createInstances(cluster);
        assertThat(instanceBOs.size()).isEqualTo(6);

        int freeMemoryCount = 0;
        for (Node node : nodes) {
            node = nodeEntityService.getById(node.getId());
            freeMemoryCount += node.getFreeMemory();
        }
        int cacheMemorySum = cluster.getCacheSize() * (cluster.getMasterCount() * (cluster.getReplicaCount() + 1));
        assertThat(originFreeMemoryCount - freeMemoryCount).isEqualTo(cacheMemorySum);

        instanceBOs.stream().forEach(each -> {
            assertThat(each.getSelf().getId()).isNotNull();
        });

        instanceBOs.stream().filter(each -> each.getMaster() != null).forEach(each -> {
            assertThat(each.getMaster().getSelf().getNodeId()).isNotEqualTo(each.getSelf().getNodeId());
        });
    }

    @Test
    @Transactional
    public void createInstancesWithSentinel() {
        List<Node> nodes = buildNodes();
        int originFreeMemoryCount = nodes.stream().mapToInt(Node::getFreeMemory).sum();
        Cluster cluster = buildRedisCluster();
        cluster.setRedisMode(RedisMode.STANDALONE);
        cluster.setEnableSentinel(true);
        List<InstanceBO> instanceBOs = instanceProcessService.createInstances(cluster);
        assertThat(instanceBOs.size()).isEqualTo(12);

        int freeMemoryCount = 0;
        for (Node node : nodes) {
            node = nodeEntityService.getById(node.getId());
            freeMemoryCount += node.getFreeMemory();
        }
        int cacheMemorySum = cluster.getCacheSize() * (cluster.getMasterCount() * (cluster.getReplicaCount() + 1));
        assertThat(originFreeMemoryCount - freeMemoryCount).isEqualTo(cacheMemorySum);

        instanceBOs.stream().forEach(each -> {
            assertThat(each.getSelf().getId()).isNotNull();
        });

        instanceBOs.stream().filter(each -> each.getMaster() != null).forEach(each -> {
            if (each.getSelf().getType().equals(InstanceType.SENTINEL)) {
                assertThat(each.getMaster().getSelf().getRole().equals(RedisReplicationRole.MASTER));
            } else {
                assertThat(each.getMaster().getSelf().getNodeId()).isNotEqualTo(each.getSelf().getNodeId());
            }
        });
    }

    @Test
    @Transactional
    public void queryAndUpdateRedisClusterNodeId() {
        ClusterBO redisClusterBO = buildRedisClusterBO();
        List<ClusterNodeBO> clusterNodeBOs = buildClusterNodeBOs(redisClusterBO.getInstanceBOs());
        Mockito.doReturn(clusterNodeBOs).when(redisClientUtilityService).clusterNodes(ArgumentMatchers.any());

        List<Instance> instances = redisClusterBO.getInstanceBOs().stream().map(InstanceBO::getSelf)
                .collect(Collectors.toList());
        instances.forEach(each -> {
            assertThat(Strings.isNullOrEmpty(each.getClusterNodeId()));
        });
        instanceProcessService.queryAndUpdateRedisClusterNodeId(redisClusterBO);

        instances = instanceEntityService.queryByClusterId(redisClusterBO.getCluster().getClusterId());
        instances.forEach(each -> {
            assertThat(each.getClusterNodeId()).isNotBlank();
        });
    }

    @Test
    @Transactional
    public void importRedisServerInstance() {
        ClusterBO redisClusterBO = buildRedisClusterBO();
        List<InstanceBO> allInstances = redisClusterBO.getInstanceBOs();
        List<ClusterNodeBO> clusterNodeBOs = buildClusterNodeBOs(allInstances);
        instanceEntityService.removeByIds(
                allInstances.stream().map(InstanceBO::getSelf).map(Instance::getId).collect(Collectors.toSet()));
        Mockito.doReturn(clusterNodeBOs).when(redisClientUtilityService).clusterNodes(ArgumentMatchers.any());
        Mockito.doReturn(redisClusterBO.getInstanceBOs().stream().map(InstanceBO::getNode).collect(Collectors.toList()))
                .when(ansibleService).runPlayBookOnNodes(ArgumentMatchers.any(), ArgumentMatchers.any());

        redisClusterBO.setInstanceBOs(new ArrayList<>(allInstances.subList(0, 1)));
        instanceProcessService.importInstance(redisClusterBO);

        List<Instance> instances = instanceEntityService.lambdaQuery()
                .eq(Instance::getClusterId, redisClusterBO.getCluster().getClusterId()).list();
        assertThat(instances.size()).isEqualTo(allInstances.size());
        Set<Instance> allInstancesSet = allInstances.stream().map(InstanceBO::getSelf).collect(Collectors.toSet());
        instances.forEach(each -> {
            assertThat(nodeEntityService.getById(each.getNodeId())).isNotNull();
            assertThat(each.getClusterNodeId()).isNotBlank();
            allInstancesSet.contains(each);
        });
    }

    @Test
    @Transactional
    public void getInstanceBOById() {
        ClusterBO clusterBO = buildRedisClusterBO();
        for (Instance instance : clusterBO.getInstanceBOs().stream().map(InstanceBO::getSelf)
                .collect(Collectors.toList())) {
            if (instance.getRole().equals(RedisReplicationRole.SLAVE)) {
                InstanceBO instanceBO = instanceProcessService.getInstanceBOById(instance.getId());
                assertThat(instanceBO.getMaster()).isNotNull();
                assertThat(instanceBO.getMaster().getSelf()).isNotNull();
                assertThat(instanceBO.getMaster().getNode()).isNotNull();
            }
        }
    }

    public ClusterBO buildRedisClusterBO() {
        Cluster cluster = buildRedisCluster();
        List<Node> nodes = buildNodes();
        List<InstanceBO> instanceBOs = instanceProcessService.createInstances(cluster);
        return ClusterBO.builder().cluster(cluster).instanceBOs(instanceBOs).build();
    }

    public Cluster buildRedisCluster() {
        Cluster pojo = new Cluster();
        pojo.setClusterId("Hr5bGKBQ");
        pojo.setRedisMode(RedisMode.CLUSTER);
        pojo.setPassword("b840fc02d524045429941cc15f59e41cb7be6c52");
        pojo.setDescription("test");
        pojo.setEnableSentinel(false);
        pojo.setEnv(Environment.TEST);
        pojo.setIdc(InternetDataCenter.IDC4);
        pojo.setCacheSize(1);
        pojo.setMasterCount(3);
        pojo.setRedisVersion("redis-3.2.11");
        pojo.setReplicaCount(1);
        pojo.setUserName("nobody");
        pojo.setUserCnName("无名氏");
        pojo.setStatus(ClusterStatus.UP);
        clusterEntityService.save(pojo);
        return pojo;
    }

    public List<Node> buildNodes() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(buildNode(1, "192.168.1.1", 3));
        nodes.add(buildNode(2, "192.168.1.2", 3));
        nodes.add(buildNode(3, "192.168.1.3", 2));
        nodes.add(buildNode(4, "192.168.1.4", 1));
        nodes.add(buildNode(5, "192.168.1.5", 1));
        nodeEntityService.saveBatch(nodes);
        return nodes;
    }

    public Node buildNode(Integer id, String ip, int freeMemory) {
        Node node = new Node();
        node.setId(id);
        node.setIp(ip);
        node.setHost(ip);
        node.setTotalMemory(freeMemory);
        node.setFreeMemory(freeMemory);
        node.setCreationDate(new Date());
        node.setLastChangedDate(node.getCreationDate());
        node.setEnv(Environment.TEST);
        node.setIdc(InternetDataCenter.IDC4);
        node.setPassword("password");
        node.setStatus(NodeStatus.INITIALIZED);
        return node;
    }

    public List<ClusterNodeBO> buildClusterNodeBOs(List<InstanceBO> instanceBOs) {
        List<ClusterNodeBO> clusterNodeBOs = new ArrayList<>();
        instanceBOs.forEach(each -> {
            clusterNodeBOs.add(ClusterNodeBO.builder().clusterNodeId(RandomStringUtils.random(40, true, true))
                    .ip(each.getNode().getIp()).port(each.getSelf().getPort()).role(each.getSelf().getRole()).build());
        });
        return clusterNodeBOs;
    }
}
