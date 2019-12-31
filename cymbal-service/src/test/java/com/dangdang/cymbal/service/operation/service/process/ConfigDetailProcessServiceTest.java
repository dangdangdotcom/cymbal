package com.dangdang.cymbal.service.operation.service.process;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.domain.po.ConfigDetailStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDetailEntityService;
import com.dangdang.cymbal.service.operation.service.entity.ConfigEntityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigDetailProcessServiceTest {

    @Resource
    NodeEntityService nodeEntityService;

    @Resource
    ConfigEntityService redisConfigEntityService;

    @Resource
    ConfigDetailEntityService redisConfigDetailEntityService;

    @Resource
    InstanceProcessService instanceProcessService;

    @Resource
    ConfigDetailProcessService redisConfigDetailProcessService;

    @MockBean
    RedisClientUtilityService redisClientUtilityService;

    @Before
    public void setUp() {
    }

    @Test
    public void updatePersistenceType() {
        Config config = buildRedisConfig();
        redisConfigDetailProcessService.updatePersistenceType(config, RedisPersistenceType.RDB);

        List<ConfigDetail> configDetailList = redisConfigDetailEntityService.lambdaQuery()
                .eq(ConfigDetail::getConfigId, config.getId()).list();
        assertThat(configDetailList.size()).isEqualTo(1);
        assertThat(configDetailList.get(0).getStatus()).isEqualTo(ConfigDetailStatus.EFFECTIVE);
    }

    @Test
    public void effectConfigDetails() {

    }

    @Test
    public void createConfigDetailsForImportedRedisCluster() {
        Mockito.doReturn(Arrays.asList("save", "1000 6 100 6", "maxmemory-policy", "volatile-lru"))
                .when(redisClientUtilityService)
                .executeRedisCommand(Mockito.any(InstanceBO.class), Mockito.any(), Mockito.any());
        Config config = buildRedisConfig();
        ClusterBO redisClusterBO = buildRedisClusterBO();
        redisConfigDetailProcessService.createConfigDetailsForImportedRedisCluster(config, redisClusterBO);

        List<ConfigDetail> configDetails = redisConfigDetailEntityService.lambdaQuery()
                .eq(ConfigDetail::getConfigId, config.getId()).eq(ConfigDetail::getStatus, ConfigDetailStatus.EFFECTIVE)
                .list();
        assertThat(configDetails.size()).isEqualTo(2);
        configDetails.forEach(each -> {
            switch (each.getItemName()) {
                case "save":
                    assertThat(each.getItemValue()).isEqualTo("\"1000 6 100 6\"");
                    break;
                case "maxmemory-policy":
                    assertThat(each.getItemValue()).isEqualTo("volatile-lru");
                    break;
                default:
                    throw new RuntimeException();
            }
        });
    }

    public Config buildRedisConfig() {
        Config config = new Config();
        config.setClusterId("12345678");
        config.setConfigName("test-redis");
        config.setCreationDate(new Date());
        config.setLastChangedDate(new Date());
        config.setUserName("Guns'N'Roses");
        config.setUserCnName("枪炮玫瑰");
        config.setRedisVersion("redis-3.2.11");
        redisConfigEntityService.save(config);
        return config;
    }

    public ClusterBO buildRedisClusterBO() {
        buildNodes();
        Cluster cluster = buildRedisCluster();
        List<InstanceBO> instanceBOs = instanceProcessService.createInstances(cluster);
        return ClusterBO.builder().cluster(cluster).instanceBOs(instanceBOs).build();
    }

    public Cluster buildRedisCluster() {
        Cluster pojo = new Cluster();
        pojo.setClusterId("Hr5bGKBQ");
        pojo.setRedisMode(RedisMode.CLUSTER);
        pojo.setPassword("b840fc02d524045429941cc15f59e41cb7be6c52");
        pojo.setDescription("test");
        pojo.setEnableSentinel(true);
        pojo.setEnv(Environment.TEST);
        pojo.setIdc(InternetDataCenter.TEST);
        pojo.setCacheSize(1);
        pojo.setMasterCount(3);
        pojo.setRedisVersion("redis-3.2.11");
        pojo.setReplicaCount(1);
        pojo.setUserName("Jimi Hendrix");
        pojo.setUserCnName("吉米·亨德里克斯");
        pojo.setStatus(ClusterStatus.UP);
        return pojo;
    }

    private List<Node> buildNodes() {
        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Node node = new Node();
            node.setEnv(Environment.TEST);
            node.setFreeMemory(3);
            node.setIp("192.168.1." + i);
            node.setIdc(InternetDataCenter.TEST);
            node.setStatus(NodeStatus.INITIALIZED);
            node.setHost("weezer.com");
            node.setDescription("威泽尔乐团");
            node.setLastChangedDate(new Date());
            node.setCreationDate(new Date());
            nodes.add(node);
        }
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
        node.setStatus(NodeStatus.UNINITIALIZED);
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