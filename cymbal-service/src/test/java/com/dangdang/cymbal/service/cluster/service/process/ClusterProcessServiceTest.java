package com.dangdang.cymbal.service.cluster.service.process;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.operation.enums.RedisCommand;
import com.dangdang.cymbal.service.operation.enums.RedisReplyFormat;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDetailEntityService;
import com.dangdang.cymbal.service.operation.service.entity.ConfigEntityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.enums.ShellCommand;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.dangdang.cymbal.service.util.service.SshClientService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterProcessServiceTest {

    @Resource
    private ClusterProcessService clusterProcessService;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private NodeEntityService nodeEntityService;

    @Resource
    private InstanceEntityService instanceEntityService;

    @Resource
    private ConfigEntityService redisConfigEntityService;

    @Resource
    private ConfigDetailEntityService redisConfigDetailEntityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @MockBean
    private AnsibleService ansibleService;

    @MockBean
    private RedisShellUtilityService redisShellUtilityService;

    @MockBean
    private SshClientService sshClientService;

    @Before
    public void setup() {
        List<String> clusterNodes = Lists.newArrayList(
                "adfa5a7fd3ae38358e0ebcb917cd397c3088f01f 192.168.1.1:8381@17000 myself,master - 0 0 1 connected 0-5460",
                "e3837d23375f66994c3a72ed3198d4d3d738813e 192.168.1.1:8382@17004 slave 2e70ba28d9049459c38698b32029185979f9ecfb 0 1458128356219 2 connected",
                "2e70ba28d9049459c38698b32029185979f9ecfb 192.168.1.2:8381@17001 master - 0 1458128353214 2 connected 5461-10922",
                "51be946e224265780f7bdb98a47f0b0d426e4122 192.168.1.2:8382@17005 slave 9fc4a4bc97278a05464504fe0ee975b0f78d549b 0 1458128352213 3 connected",
                "8a5dac298cec6f37ee7bc996a393792156629023 192.168.1.3:8381@17003 slave adfa5a7fd3ae38358e0ebcb917cd397c3088f01f 0 1458128354215 1 connected",
                "9fc4a4bc97278a05464504fe0ee975b0f78d549b 192.168.1.3:8382@17002 master - 0 1458128355217 3 connected 10923-16383");
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(redisShellUtilityService)
                .executeSentinelShellScript(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(clusterNodes).when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.eq(ShellCommand.REDIS_CLI_CMD),
                        ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.contains(RedisCommand.CLUSTER_NODES.getValue()),
                        ArgumentMatchers.eq(RedisReplyFormat.RAW.getValue()));
    }

    @Test
    @Transactional
    public void createRedisClusterByRedisApplicationFormOfCluster() {
        buildNodes(3);

        ApplicationForm applicationForm = buildApplicationForm();
        String clusterId = clusterProcessService.createRedisClusterByRedisApplicationForm(applicationForm);

        assertThat(clusterId).isNotBlank();

        List<Instance> instances = instanceEntityService.lambdaQuery().eq(Instance::getClusterId, clusterId).list();
        assertThat(instances.size())
                .isEqualTo(applicationForm.getMasterCount() * (applicationForm.getReplicaCount() + 1));

        instances.forEach(each -> {
            assertThat(each.getClusterNodeId()).isNotBlank();

            if (each.getRole().equals(RedisReplicationRole.SLAVE)) {
                assertThat(each.getSlaveof()).isNotBlank();
            }
        });

        Config config = redisConfigEntityService.lambdaQuery().ge(Config::getClusterId, clusterId).one();
        assertThat(config).isNotNull();

        List<ConfigDetail> configDetails = redisConfigDetailEntityService.lambdaQuery()
                .ge(ConfigDetail::getConfigId, config.getId()).list();
        assertThat(configDetails.size()).isEqualTo(1);
        assertThat(configDetails.get(0).getItemValue()).isEqualTo(Constant.RedisConfig.APPENDONLY_NO);
    }

    @Test
    @Transactional
    public void createRedisClusterByImport() {
        ClusterBO redisClusterBO = buildRedisClusterBO();
        List<InstanceBO> allInstances = redisClusterBO.getInstanceBOs();
        Mockito.doReturn(redisClusterBO.getInstanceBOs().stream().map(InstanceBO::getNode).collect(Collectors.toList()))
                .when(ansibleService).runPlayBookOnNodes(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList("save", "1000 6 100 6", "maxmemory-policy", "volatile-lru"))
                .when(redisShellUtilityService)
                .executeRedisShellScript(ArgumentMatchers.any(), ArgumentMatchers.eq(ShellCommand.REDIS_CLI_CMD),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq("config get \\*"),
                        ArgumentMatchers.any());
        redisClusterBO.setInstanceBOs(new ArrayList<>(allInstances.subList(0, 1)));

        clusterProcessService.createRedisClusterByImport(redisClusterBO);

        // Assert for cluster.
        Cluster cluster = clusterEntityService.getByClusterId(redisClusterBO.getCluster().getClusterId());
        assertThat(cluster).isNotNull();
        assertThat(cluster.getMasterCount() * (cluster.getReplicaCount() + 1)).isEqualTo(allInstances.size());

        // Assert for redis server instance.
        List<Instance> instances = instanceEntityService.lambdaQuery()
                .eq(Instance::getClusterId, cluster.getClusterId()).list();
        assertThat(instances.size()).isEqualTo(allInstances.size());
        Set<Instance> allInstancesSet = allInstances.stream().map(InstanceBO::getSelf).collect(Collectors.toSet());
        instances.forEach(each -> {
            assertThat(nodeEntityService.getById(each.getNodeId())).isNotNull();
            assertThat(each.getClusterNodeId()).isNotBlank();
            allInstancesSet.contains(each);
        });

        // Assert for config.
        Config config = redisConfigEntityService.lambdaQuery().eq(Config::getClusterId, cluster.getClusterId()).one();
        assertThat(config).isNotNull();
        List<ConfigDetail> configDetails = redisConfigDetailEntityService.lambdaQuery()
                .eq(ConfigDetail::getConfigId, config.getId()).list();
        assertThat(configDetails.size()).isEqualTo(2);

        // Assert for monitor.
        Mockito.verify(sshClientService, Mockito.atLeastOnce())
                .createNewFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        // Assert for node.
        List<Node> nodes = nodeEntityService.lambdaQuery().likeRight(Node::getIp, "192.168.1.").list();
        assertThat(nodes.size()).isEqualTo(3);
    }

    @Test
    public void queryAllRedisClusters() {
        buildNodes(10);
        ApplicationForm applicationForm = buildApplicationForm();
        clusterProcessService.createRedisClusterByRedisApplicationForm(applicationForm);
        clusterProcessService.createRedisClusterByRedisApplicationForm(applicationForm);

        List<ClusterBO> redisClusterBOs = clusterProcessService.queryAllRedisClusters();
        assertThat(redisClusterBOs.size()).isEqualTo(2);

        redisClusterBOs.forEach(each -> {
            assertThat(each.getInstanceBOs().size())
                    .isEqualTo(applicationForm.getMasterCount() * (applicationForm.getReplicaCount() + 1));
            each.getInstanceBOs().forEach(eachRedisServerInstanceBO -> {
                if (RedisReplicationRole.SLAVE.equals(eachRedisServerInstanceBO.getSelf().getRole())) {
                    assertThat(eachRedisServerInstanceBO.getMaster()).isNotNull();
                }
            });
        });
    }

    public ClusterBO buildRedisClusterBO() {
        Cluster cluster = buildRedisCluster();
        buildNodes(3);
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
        pojo.setUserName("Trivium");
        pojo.setUserCnName("三学科");
        pojo.setStatus(ClusterStatus.UP);
        return pojo;
    }

    private List<Node> buildNodes(final int freeMemory) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Node node = new Node();
            node.setEnv(Environment.TEST);
            node.setFreeMemory(freeMemory);
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

    private ApplicationForm buildApplicationForm() {
        ApplicationForm pojo = new ApplicationForm();
        pojo.setApplicantEnName("Mr.White");
        pojo.setApplicantCnName("大白鲨");
        pojo.setEnv(Environment.TEST);
        pojo.setIdc(InternetDataCenter.TEST);
        pojo.setRedisMode(RedisMode.CLUSTER);
        pojo.setRedisVersion("4.0.12");
        pojo.setCacheSize(1);
        pojo.setMasterCount(3);
        pojo.setReplicaCount(1);
        pojo.setRedisPersistenceType(RedisPersistenceType.RDB);
        pojo.setEnableSentinel(false);
        pojo.setPassword("b840fc02d524045429941cc15f59e41cb7be6c52");
        pojo.setDescription("测试申请单");
        pojo.setStatus(ApplicationFormStatus.DRAFT);
        return pojo;
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
