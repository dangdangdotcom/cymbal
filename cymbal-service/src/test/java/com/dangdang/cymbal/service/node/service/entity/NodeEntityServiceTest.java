package com.dangdang.cymbal.service.node.service.entity;

import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NodeEntityServiceTest {

    @Resource
    NodeEntityService nodeEntityService;

    @Test
    public void save() {
        Node node = buildNode();
        nodeEntityService.save(node);
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
        node.setPassword("PASSWORD");
        node.setStatus(NodeStatus.UNINITIALIZED);
        return node;
    }

}