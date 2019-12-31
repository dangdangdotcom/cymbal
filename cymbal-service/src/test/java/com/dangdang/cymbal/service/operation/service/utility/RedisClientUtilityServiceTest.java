package com.dangdang.cymbal.service.operation.service.utility;

import com.dangdang.cymbal.service.util.service.impl.ShellService;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.*;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisClientUtilityServiceTest {

    @Resource
    RedisClientUtilityService redisClientUtilityService;

    @MockBean
    ShellService shellService;

    @Test
    public void startup() {

    }

    @Test
    public void slaveOf() {
    }

    @Test
    public void clusterNodes() {
        List<String> clusterNodes = Lists.newArrayList(
                "adfa5a7fd3ae38358e0ebcb917cd397c3088f01f 127.0.0.1:7000@17000 myself,master - 0 0 1 connected 0-5460",
                "e3837d23375f66994c3a72ed3198d4d3d738813e 127.0.0.1:7004@17004 slave 2e70ba28d9049459c38698b32029185979f9ecfb 0 1458128356219 2 connected",
                "2e70ba28d9049459c38698b32029185979f9ecfb 127.0.0.1:7001@17001 master - 0 1458128353214 2 connected 5461-10922",
                "51be946e224265780f7bdb98a47f0b0d426e4122 127.0.0.1:7005@17005 slave 9fc4a4bc97278a05464504fe0ee975b0f78d549b 0 1458128352213 3 connected",
                "8a5dac298cec6f37ee7bc996a393792156629023 127.0.0.1:7003@17003 slave adfa5a7fd3ae38358e0ebcb917cd397c3088f01f 0 1458128354215 1 connected",
                "9fc4a4bc97278a05464504fe0ee975b0f78d549b 127.0.0.1:7002@17002 master - 0 1458128355217 3 connected 10923-16383");
        Mockito.doReturn(clusterNodes).when(shellService).execRedisShellScript(ArgumentMatchers.any());
        List<ClusterNodeBO> redisClusterNodeBOs = redisClientUtilityService.clusterNodes(buildRedisServerInstanceBO());
        assertThat(redisClusterNodeBOs.size()).isEqualTo(6);
        redisClusterNodeBOs.forEach(each -> {
            assertThat(each.getClusterNodeId().length()).isEqualTo(40);
            assertThat(each.getIp().length()).isEqualTo(9);
            assertThat(each.getPort().toString().length()).isEqualTo(4);
            assertThat(each.getRole()).isNotNull();
        });
    }

    InstanceBO buildRedisServerInstanceBO() {
        return InstanceBO.builder().node(buildNode(1, "192.168.1.1", 3)).self(buildRedisServerInstance())
                .password("password").build();
    }

    Instance buildRedisServerInstance() {
        Instance instance = new Instance();
        instance.setPort(8381);
        instance.setRedisVersion("3.2.11");
        return instance;
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
}