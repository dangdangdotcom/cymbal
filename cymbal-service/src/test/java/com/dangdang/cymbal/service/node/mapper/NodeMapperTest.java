package com.dangdang.cymbal.service.node.mapper;

import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link NodeMapper}
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class NodeMapperTest {

    @Resource
    NodeMapper nodeMapper;

    @Test
    public void insert() {
        Node node = buildNode();
        nodeMapper.insert(node);
        assertThat(node.getId()).isEqualTo(4);
    }

    private Node buildNode() {
        Node node = new Node();
        node.setIp("192.168.1.1");
        node.setHost("test.idc");
        node.setTotalMemory(16);
        node.setFreeMemory(16);
        node.setIdc(InternetDataCenter.TEST);
        node.setEnv(Environment.TEST);
        node.setPassword("dell1950");
        node.setStatus(NodeStatus.UNINITIALIZED);

        return node;
    }
}