package com.dangdang.cymbal.job.replication;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.AlarmLevel;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.service.util.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterReplicationCheckJobTest {

    @Resource
    RedisClusterReplicationCheckJob redisClusterReplicationCheckJob;

    @MockBean
    ClusterProcessService clusterProcessService;

    @MockBean
    MailService mailService;

    @Test
    public void execute() {
        Mockito.doReturn(Arrays.asList(buildRedisClusterBO())).when(clusterProcessService).queryAllRedisClusters();
        redisClusterReplicationCheckJob.execute();
        Mockito.verify(mailService, Mockito.times(0)).sendHtmlMailToAdmin(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void executeOfAlarm() {
        ClusterBO redisClusterBO = buildRedisClusterBO();
        Mockito.doReturn(Arrays.asList(redisClusterBO)).when(clusterProcessService).queryAllRedisClusters();
        redisClusterBO.getInstanceBOs().forEach(each -> each.getSelf().setRole(RedisReplicationRole.MASTER));
        redisClusterReplicationCheckJob.execute();
        Mockito.verify(mailService, Mockito.times(1)).sendHtmlMailToAdmin(Mockito.anyString(), Mockito.anyString());
    }

    public ClusterBO buildRedisClusterBO() {
        Cluster cluster = buildRedisCluster();
        List<Node> nodes = buildNodes();
        List<InstanceBO> instanceBOS = buildRedisServerInstanceBOs(nodes);
        return ClusterBO.builder().cluster(cluster).instanceBOs(instanceBOS).build();
    }

    public Cluster buildRedisCluster() {
        Cluster pojo = new Cluster();
        pojo.setClusterId("Hr5bGKBQ");
        pojo.setAlarmLevel(AlarmLevel.ALARM);
        return pojo;
    }

    private List<Node> buildNodes() {
        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Node node = new Node();
            node.setId(i);
            node.setIp("192.168.1." + i);
            nodes.add(node);
        }
        return nodes;
    }

    private List<InstanceBO> buildRedisServerInstanceBOs(List<Node> nodes) {
        List<InstanceBO> instanceBOS = new ArrayList<>();
        for (Node node : nodes) {
            Instance instance = new Instance();
            instance.setNodeId(node.getId());
            instance.setRole(RedisReplicationRole.MASTER);
            InstanceBO instanceBO = InstanceBO.builder().node(node).self(instance).build();
            instanceBOS.add(instanceBO);

            instance = new Instance();
            instance.setNodeId(node.getId());
            instance.setRole(RedisReplicationRole.SLAVE);
            instanceBO = InstanceBO.builder().node(node).self(instance).build();
            instanceBOS.add(instanceBO);
        }
        return instanceBOS;
    }
}