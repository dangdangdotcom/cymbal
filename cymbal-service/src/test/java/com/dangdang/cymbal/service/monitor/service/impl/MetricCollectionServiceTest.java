package com.dangdang.cymbal.service.monitor.service.impl;

import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.service.monitor.service.MetricCollectionService;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.dangdang.cymbal.service.util.service.SshClientService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for {@link MetricCollectionService}
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricCollectionServiceTest {

    @Resource
    private MetricCollectionService metricCollectionService;

    @MockBean
    private SshClientService sshClientService;

    @MockBean
    private AnsibleService ansibleService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(metricCollectionService, "sshClientService", sshClientService);
    }

    @Test
    public void addNode() {
    }

    @Test
    public void addNodes() {
        List<Node> nodesToAdd = buildNodes();
        Mockito.doReturn(nodesToAdd).when(ansibleService).runPlayBookOnNodes(Mockito.any(), Mockito.any());
        metricCollectionService.addNodes(buildNodes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNodesWithEmptyList() {
        metricCollectionService.addNodes(Lists.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNodesWithNullList() {
        metricCollectionService.addNodes(null);
    }

    @Test
    public void addRedisServerInstance() {
    }

    @Test
    public void addRedisServerInstances() {
    }

    private List<Node> buildNodes() {
        List<Node> nodes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Node node = new Node();
            node.setIp("192.168.1." + i);
            node.setHost("test.idc");
            node.setTotalMemory(16);
            node.setFreeMemory(16);
            node.setIdc(InternetDataCenter.TEST);
            node.setEnv(Environment.TEST);
            node.setPassword("PASSWORD");
            node.setStatus(NodeStatus.UNINITIALIZED);
            nodes.add(node);
        }
        return nodes;
    }
}
