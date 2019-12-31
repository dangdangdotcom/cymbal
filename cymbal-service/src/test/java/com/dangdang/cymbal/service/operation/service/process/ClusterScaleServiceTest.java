package com.dangdang.cymbal.service.operation.service.process;

import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.monitor.service.MonitorService;
import com.dangdang.cymbal.service.node.exception.NotEnoughResourcesException;
import com.dangdang.cymbal.service.operation.service.entity.ClusterScaleEntityService;
import com.dangdang.cymbal.service.util.service.impl.ShellService;
import com.dangdang.cymbal.domain.po.*;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.google.common.collect.Lists;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterScaleServiceTest {

    @Resource
    ClusterScaleProcessService redisClusterScaleProcessService;

    @Resource
    NodeEntityService nodeEntityService;

    @Resource
    InstanceEntityService instanceEntityService;

    @Resource
    ClusterScaleEntityService redisClusterScaleEntityService;

    @Resource
    InstanceProcessService instanceProcessService;

    @Resource
    ClusterEntityService clusterEntityService;

    @Resource
    ConfigProcessService redisConfigProcessService;

    @MockBean
    MonitorService monitorService;

    @MockBean
    ShellService shellService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void horizontalScaleForCluster() {
        // TODO: How to do unit test with multi threads?

    }

    @Test
    public void horizontalScaleForStandalone() {
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(shellService).execRedisShellScript(ArgumentMatchers.any());

        Cluster standaloneCluster = buildRedisCluster();
        standaloneCluster.setRedisMode(RedisMode.STANDALONE);
        standaloneCluster.setMasterCount(2);
        standaloneCluster.setReplicaCount(2);
        clusterEntityService.updateById(standaloneCluster);
        buildNodes();
        instanceProcessService.createInstances(standaloneCluster);
        redisConfigProcessService.createConfigForNewRedisCluster(standaloneCluster, RedisPersistenceType.RDB);

        ClusterScale standaloneHorizontalScale = buildRedisClusterScale();
        standaloneHorizontalScale.setClusterId(standaloneCluster.getClusterId());
        standaloneHorizontalScale.setScaleNum(2);
        redisClusterScaleProcessService.doScale(standaloneHorizontalScale);

        assertThat(standaloneHorizontalScale.getStatus(), is(ScaleStatus.DONE));
        assertThat(standaloneHorizontalScale.getResult(), is(ScaleResult.SUCCESS));
        List<Instance> nodeInstanceInfos = instanceEntityService.queryByClusterId(standaloneCluster.getClusterId());
        assertThat(nodeInstanceInfos.size(), is(12));

        Cluster clusterScaled = clusterEntityService.getByClusterId(standaloneCluster.getClusterId());
        assertThat(clusterScaled.getMasterCount(), is(4));
        assertThat(clusterScaled.getReplicaCount(), is(2));
    }

    @Test(expected = NotEnoughResourcesException.class)
    @Transactional
    public void horizontalScaleForStandaloneNotEnoughResources() {
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(shellService).execRedisShellScript(ArgumentMatchers.any());

        Cluster standaloneCluster = buildRedisCluster();
        standaloneCluster.setRedisMode(RedisMode.STANDALONE);
        standaloneCluster.setMasterCount(100);
        standaloneCluster.setReplicaCount(2);
        clusterEntityService.updateById(standaloneCluster);
        buildNodes();
        instanceProcessService.createInstances(standaloneCluster);
        redisConfigProcessService.createConfigForNewRedisCluster(standaloneCluster, RedisPersistenceType.RDB);

        ClusterScale standaloneHorizontalScale = buildRedisClusterScale();
        standaloneHorizontalScale.setClusterId(standaloneCluster.getClusterId());
        standaloneHorizontalScale.setScaleNum(2);
        redisClusterScaleProcessService.doScale(standaloneHorizontalScale);

        assertThat(standaloneHorizontalScale.getStatus(), is(ScaleStatus.DONE));
        assertThat(standaloneHorizontalScale.getResult(), is(ScaleResult.SUCCESS));
        List<Instance> nodeInstanceInfos = instanceEntityService.queryByClusterId(standaloneCluster.getClusterId());
        assertThat(nodeInstanceInfos.size(), is(12));

        Cluster clusterScaled = clusterEntityService.getByClusterId(standaloneCluster.getClusterId());
        assertThat(clusterScaled.getMasterCount(), is(4));
        assertThat(clusterScaled.getReplicaCount(), is(2));
    }

    @Test
    public void verticalScale() {
        Cluster standalone = buildRedisCluster();
        standalone.setMasterCount(3);
        standalone.setReplicaCount(1);
        clusterEntityService.updateById(standalone);

        buildNodes();
        instanceProcessService.createInstances(standalone);

        ClusterScale clusterScale = buildRedisClusterScale();
        clusterScale.setClusterId(standalone.getClusterId());
        clusterScale.setType(ScaleType.VERTICAL);
        clusterScale.setScaleNum(1);
        redisClusterScaleProcessService.doScale(clusterScale);

        assertThat(clusterScale.getStatus(), is(ScaleStatus.DONE));
        assertThat(clusterScale.getResult(), is(ScaleResult.SUCCESS));

        Cluster clusterScaled = clusterEntityService.getByClusterId(clusterScale.getClusterId());
        assertThat(clusterScaled.getCacheSize(), is(2));
    }

    @Test(expected = NotEnoughResourcesException.class)
    public void verticalScaleNotEnoughResources() {
        Cluster standalone = buildRedisCluster();
        standalone.setRedisMode(RedisMode.STANDALONE);
        standalone.setMasterCount(3);
        standalone.setReplicaCount(1);
        clusterEntityService.updateById(standalone);

        buildNodes();
        instanceProcessService.createInstances(standalone);

        ClusterScale clusterScale = buildRedisClusterScale();
        clusterScale.setClusterId(standalone.getClusterId());
        clusterScale.setType(ScaleType.VERTICAL);
        clusterScale.setScaleNum(100);
        redisClusterScaleProcessService.doScale(clusterScale);

        assertThat(clusterScale.getStatus(), is(ScaleStatus.DONE));
        assertThat(clusterScale.getResult(), is(ScaleResult.SUCCESS));

        Cluster clusterScaled = clusterEntityService.getByClusterId(clusterScale.getClusterId());
        assertThat(clusterScaled.getCacheSize(), is(2));
    }

    @Test
    public void slaveOnlyScaleForStandalone() {
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(shellService).execRedisShellScript(ArgumentMatchers.any());

        Cluster standalone = buildRedisCluster();
        standalone.setRedisMode(RedisMode.STANDALONE);
        standalone.setMasterCount(2);
        standalone.setReplicaCount(1);
        clusterEntityService.updateById(standalone);

        buildNodes();
        instanceProcessService.createInstances(standalone);

        ClusterScale clusterScale = buildRedisClusterScale();
        clusterScale.setClusterId(standalone.getClusterId());
        clusterScale.setType(ScaleType.SLAVE_ONLY);
        clusterScale.setScaleNum(1);
        redisClusterScaleProcessService.doScale(clusterScale);

        Cluster clusterScaled = clusterEntityService.getByClusterId(standalone.getClusterId());
        assertThat(clusterScaled.getMasterCount(), is(2));
        assertThat(clusterScaled.getReplicaCount(), is(2));

        List<Instance> instances = instanceEntityService.queryByClusterId(standalone.getClusterId());
        assertThat(instances.size(), is(6));
    }

    @Test(expected = NotEnoughResourcesException.class)
    public void slaveOnlyScaleForStandaloneNotEnoughResources() {
        Mockito.doReturn(Arrays.asList("OK", "OK")).when(shellService).execRedisShellScript(ArgumentMatchers.any());

        Cluster standalone = buildRedisCluster();
        standalone.setRedisMode(RedisMode.STANDALONE);
        standalone.setMasterCount(2);
        standalone.setReplicaCount(1);
        clusterEntityService.updateById(standalone);

        buildNodes();
        instanceProcessService.createInstances(standalone);

        ClusterScale clusterScale = buildRedisClusterScale();
        clusterScale.setClusterId(standalone.getClusterId());
        clusterScale.setType(ScaleType.SLAVE_ONLY);
        clusterScale.setScaleNum(100);
        redisClusterScaleProcessService.doScale(clusterScale);

        Cluster clusterScaled = clusterEntityService.getByClusterId(standalone.getClusterId());
        assertThat(clusterScaled.getMasterCount(), is(2));
        assertThat(clusterScaled.getReplicaCount(), is(2));

        List<Instance> instances = instanceEntityService.queryByClusterId(standalone.getClusterId());
        assertThat(instances.size(), is(6));
    }

    @Test
    public void slaveOnlyScaleForCluster() {
        List<String> clusterNodes = Lists.newArrayList(
                "adfa5a7fd3ae38358e0ebcb917cd397c3088f01f 192.168.1.1:8381@17000 myself,master - 0 0 1 connected 0-5460",
                "e3837d23375f66994c3a72ed3198d4d3d738813e 192.168.1.1:8382@17004 slave 2e70ba28d9049459c38698b32029185979f9ecfb 0 1458128356219 2 connected",
                "2e70ba28d9049459c38698b32029185979f9ecfb 192.168.1.2:8381@17001 master - 0 1458128353214 2 connected 5461-10922",
                "51be946e224265780f7bdb98a47f0b0d426e4122 192.168.1.2:8382@17005 slave 9fc4a4bc97278a05464504fe0ee975b0f78d549b 0 1458128352213 3 connected",
                "8a5dac298cec6f37ee7bc996a393792156629023 192.168.1.3:8381@17003 slave adfa5a7fd3ae38358e0ebcb917cd397c3088f01f 0 1458128354215 1 connected",
                "9fc4a4bc97278a05464504fe0ee975b0f78d549b 192.168.1.3:8382@17002 master - 0 1458128355217 3 connected 10923-16383");
        Mockito.doReturn(clusterNodes).when(shellService).execRedisShellScript(ArgumentMatchers.any());

        Cluster cluster = buildRedisCluster();
        buildNodes();
        instanceProcessService.createInstances(cluster);

        ClusterScale clusterScale = buildRedisClusterScale();
        clusterScale.setClusterId(cluster.getClusterId());
        clusterScale.setType(ScaleType.SLAVE_ONLY);
        clusterScale.setScaleNum(1);
        redisClusterScaleProcessService.doScale(clusterScale);

        Cluster clusterScaled = clusterEntityService.getByClusterId(cluster.getClusterId());
        assertThat(clusterScaled.getMasterCount(), is(cluster.getMasterCount()));
        assertThat(clusterScaled.getReplicaCount(), is(cluster.getReplicaCount() + clusterScale.getScaleNum()));

        List<Instance> instances = instanceEntityService.queryByClusterId(cluster.getClusterId());
        assertThat(instances.size(), is(9));
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
        clusterEntityService.save(pojo);
        return pojo;
    }

    private ClusterScale buildRedisClusterScale() {
        ClusterScale clusterScale = new ClusterScale();
        clusterScale.setClusterId("Hr5bGKBQ");
        clusterScale.setOperator("Mr.Big");
        clusterScale.setScaleNum(3);
        clusterScale.setStatus(ScaleStatus.DOING);
        clusterScale.setType(ScaleType.HORIZONTAL);
        return clusterScale;
    }
}
