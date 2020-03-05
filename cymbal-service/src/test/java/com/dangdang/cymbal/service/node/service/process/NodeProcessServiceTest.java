package com.dangdang.cymbal.service.node.service.process;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.util.enums.AnsiblePlayBookName;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.dangdang.cymbal.service.util.service.impl.ShellService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link NodeProcessService}.
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NodeProcessServiceTest {

    @Resource
    NodeProcessService nodeProcessService;

    @Resource
    NodeEntityService nodeEntityService;

    @Resource
    AnsibleService ansibleService;

    @MockBean
    ShellService shellService;

    @Before
    public void setUp() {
        List<String> ansibleResult = Lists
                .newArrayList("TASK [make redis] **************************************************************\n",
                        "changed: [192.168.91.182] => (item=redis-2.8.24)\n",
                        "changed: [192.168.91.200] => (item=redis-2.8.24)\n",
                        "changed: [192.168.91.182] => (item=redis-3.2.11)\n",
                        "changed: [192.168.91.200] => (item=redis-3.2.11)\n",
                        "PLAY RECAP *********************************************************************\n",
                        "192.168.1.1             : ok=7    changed=2    unreachable=0    failed=0\n",
                        "192.168.1.2             : ok=7    changed=2    unreachable=0    failed=0\n",
                        "192.168.1.3             : ok=6    changed=2    unreachable=0    failed=1\n");
        Mockito.doReturn(ansibleResult).when(shellService)
                .execAnsibleShellScript(Mockito.any(AnsiblePlayBookName.class), Mockito.anyString());
        ReflectionTestUtils.setField(ansibleService, "shellService", shellService);
    }

    @Test
    public void addNodesFromExcel() {
        int originSize = nodeEntityService.list().size();
        String filePath = ClassLoader.getSystemResource("attachment/nodes.xlsx").getPath().substring(1);
        int addedSize = nodeProcessService.createNodesFromExcel(filePath);
        int newSize = nodeEntityService.list().size();

        assertThat(newSize).isEqualTo(originSize + addedSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNodesFromExcelWithWrongFileName() {
        String filePath = ClassLoader.getSystemResource("attachment/nodes.xlsx").getPath().substring(1) + "1";
        nodeProcessService.createNodesFromExcel(filePath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNodesFromExcelWithNullPath() {
        nodeProcessService.createNodesFromExcel(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNodesFromExcelWithNotExistFile() {
        nodeProcessService.createNodesFromExcel("attachment/notexist.xlsx");
    }

    @Test
    public void initNodes() {
        List<Node> nodes = nodeEntityService
                .list(new QueryWrapper<Node>().lambda().eq(Node::getStatus, NodeStatus.UNINITIALIZED));
        List<Node> initedNodes = nodeProcessService.initNodes(nodes);
        assertThat(initedNodes.size()).isEqualTo(1);

        nodes = nodeEntityService.list(new QueryWrapper<Node>().lambda().eq(Node::getStatus, NodeStatus.UNINITIALIZED));
        assertThat(nodes.size()).isEqualTo(1);
    }
}