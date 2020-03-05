package com.dangdang.cymbal.service.cluster.service.entity;

import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test for {@link InstanceEntityService}.
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InstanceEntityServiceTest {

    @Resource
    InstanceEntityService instanceEntityService;

    @Test
    public void save() {
        Instance instance = buildRedisServerInstance();
        instanceEntityService.save(instance);
        assertThat(instance.getId()).isEqualTo(1);
    }

    @Test
    public void queryMaxPortUsingOfNode() {
        Instance instance = buildRedisServerInstance();
        Instance instance_2 = buildRedisServerInstance();
        instance_2.setPort(instance.getPort() + 1);
        instanceEntityService.save(instance);
        instanceEntityService.save(instance_2);

        assertThat(instanceEntityService.queryMaxUsedPortOfNode(instance.getNodeId())).isEqualTo(instance_2.getPort());
    }

    @Test
    public void queryMaxPortUsingOfNodeWithUnknownNodeId() {
        assertThat(instanceEntityService.queryMaxUsedPortOfNode(999)).isEqualTo(-1);
    }

    private Instance buildRedisServerInstance() {
        Instance instance = new Instance();
        instance.setClusterNodeId("5df48811e7cacd95aac98782edbdbc1faf9b3f8e");
        instance.setClusterId("ofcHRvzV");
        instance.setNodeId(100);
        instance.setPort(8381);
        instance.setRole(RedisReplicationRole.MASTER);
        instance.setSlaveof("127.0.0.1:8382");
        instance.setStatus(InstanceStatus.STARTED);
        instance.setLastChangedDate(new Date());

        return instance;
    }
}