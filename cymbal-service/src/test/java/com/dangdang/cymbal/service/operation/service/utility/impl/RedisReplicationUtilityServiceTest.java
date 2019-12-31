package com.dangdang.cymbal.service.operation.service.utility.impl;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisReplicationUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link RedisReplicationUtilityService};
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisReplicationUtilityServiceTest {

    @Resource
    RedisReplicationUtilityService redisReplicationUtilityService;

    @Resource
    InstanceProcessService instanceProcessService;

    @Resource
    NodeEntityService nodeEntityService;

    @MockBean
    RedisShellUtilityService redisShellUtilityService;

    @Test
    public void refreshReplicationToSlave() {
        ClusterBO redisClusterBO = buildRedisClusterBO();

        InstanceBO master = redisClusterBO.getInstanceBOs().get(0);
        Mockito.doReturn(Arrays.asList(RedisReplicationRole.SLAVE.getValue().toLowerCase(), master.getNode().getIp(),
                master.getSelf().getPort())).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        redisReplicationUtilityService.refreshReplication(redisClusterBO.getInstanceBOs());

        redisClusterBO.getInstanceBOs().stream().forEach(each -> {
            assertThat(each.getSelf().getRole()).isEqualTo(RedisReplicationRole.SLAVE);
            assertThat(each.getSelf().getSlaveof())
                    .isEqualTo(String.format("%s:%s", master.getNode().getIp(), master.getSelf().getPort().toString()));
        });
    }

    @Transactional
    @Test
    public void refreshReplicationToMaster() {
        ClusterBO redisClusterBO = buildRedisClusterBO();

        InstanceBO master = redisClusterBO.getInstanceBOs().get(0);
        Mockito.doReturn(Arrays.asList(RedisReplicationRole.MASTER.getValue().toLowerCase()))
                .when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        redisReplicationUtilityService.refreshReplication(redisClusterBO.getInstanceBOs());

        redisClusterBO.getInstanceBOs().stream().forEach(each -> {
            assertThat(each.getSelf().getRole()).isEqualTo(RedisReplicationRole.MASTER);
        });
    }

    @Transactional
    @Test
    public void refreshReplicationToStopped() {
        ClusterBO redisClusterBO = buildRedisClusterBO();

        InstanceBO master = redisClusterBO.getInstanceBOs().get(0);
        Mockito.doThrow(ShellExecutionException.class).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        redisReplicationUtilityService.refreshReplication(redisClusterBO.getInstanceBOs());

        redisClusterBO.getInstanceBOs().stream().forEach(each -> {
            assertThat(each.getSelf().getStatus()).isEqualTo(InstanceStatus.STOPPED);
        });
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
        pojo.setIdc(InternetDataCenter.TEST);
        pojo.setCacheSize(1);
        pojo.setMasterCount(3);
        pojo.setRedisVersion("redis-3.2.11");
        pojo.setReplicaCount(1);
        pojo.setUserName("nobody");
        pojo.setUserCnName("无名氏");
        pojo.setStatus(ClusterStatus.UP);
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
        node.setIdc(InternetDataCenter.TEST);
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
