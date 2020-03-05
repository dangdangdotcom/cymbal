package com.dangdang.cymbal.service.cluster.service.utility;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisDeployUtilityServiceTest {

    @Resource
    private DeploymentUtilityService deploymentUtilityService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @MockBean
    private RedisShellUtilityService redisShellUtilityService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void deployCluster() {
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(redisShellUtilityService)
                .executeSentinelShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        ReflectionTestUtils.setField(redisClientUtilityService, "redisShellUtilityService", redisShellUtilityService);
        deploymentUtilityService.deploy(buildRedisClusterBO());
    }

    @Test
    public void deployStandAlone() {
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(redisShellUtilityService)
                .executeSentinelShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        ReflectionTestUtils.setField(redisClientUtilityService, "redisShellUtilityService", redisShellUtilityService);
        ClusterBO redisClusterBO = buildRedisClusterBO();
        redisClusterBO.getCluster().setRedisMode(RedisMode.STANDALONE);
        deploymentUtilityService.deploy(redisClusterBO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deployStandAloneNotOk() {
        Mockito.doReturn(Arrays.asList("NOT_OK", "NOT_OK")).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList("NOT_OK", "NOT_OK")).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        ReflectionTestUtils.setField(redisClientUtilityService, "redisShellUtilityService", redisShellUtilityService);
        ClusterBO redisClusterBO = buildRedisClusterBO();
        redisClusterBO.getCluster().setRedisMode(RedisMode.STANDALONE);
        deploymentUtilityService.deploy(redisClusterBO);
    }

    public ClusterBO buildRedisClusterBO() {
        return ClusterBO.builder().cluster(buildRedisCluster()).instanceBOs(buildRedisServerinstanceBOs()).build();
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

    public List<InstanceBO> buildRedisServerinstanceBOs() {
        List<InstanceBO> instanceBOs = new ArrayList<>();

        InstanceBO instanceBO_1 = InstanceBO.builder().node(buildNode(1, "192.168.1.1", 3))
                .self(buildRedisServerInstance(8381)).build();

        InstanceBO instanceBO_2 = InstanceBO.builder().node(buildNode(1, "192.168.1.2", 3))
                .self(buildRedisServerInstance(8381)).build();

        InstanceBO instanceBO_3 = InstanceBO.builder().node(buildNode(1, "192.168.1.3", 3))
                .self(buildRedisServerInstance(8381)).build();

        InstanceBO instanceBO_4 = InstanceBO.builder().node(buildNode(1, "192.168.1.1", 3))
                .self(buildRedisServerInstance(8382)).master(instanceBO_2).build();

        InstanceBO instanceBO_5 = InstanceBO.builder().node(buildNode(1, "192.168.1.2", 3))
                .self(buildRedisServerInstance(8382)).master(instanceBO_3).build();

        InstanceBO instanceBO_6 = InstanceBO.builder().node(buildNode(1, "192.168.1.3", 3))
                .self(buildRedisServerInstance(8382)).master(instanceBO_1).build();

        instanceBOs.add(instanceBO_1);
        instanceBOs.add(instanceBO_2);
        instanceBOs.add(instanceBO_3);
        instanceBOs.add(instanceBO_4);
        instanceBOs.add(instanceBO_5);
        instanceBOs.add(instanceBO_6);

        return instanceBOs;
    }

    private Instance buildRedisServerInstance(Integer port) {
        Instance instance = new Instance();
        instance.setClusterNodeId("5df48811e7cacd95aac98782edbdbc1faf9b3f8e");
        instance.setClusterId("Hr5bGKBQ");
        instance.setNodeId(100);
        instance.setPort(port);
        instance.setRole(RedisReplicationRole.MASTER);
        instance.setSlaveof("127.0.0.1:8382");
        instance.setStatus(InstanceStatus.STARTED);
        instance.setLastChangedDate(new Date());
        instance.setRedisVersion("redis-3.2.11");
        instance.setCreationDate(new Date());
        return instance;
    }

    public Node buildNode(Integer id, String ip, int freeMemory) {
        Node node = new Node();
        node.setId(id);
        node.setIp(ip);
        node.setFreeMemory(freeMemory);
        return node;
    }
}