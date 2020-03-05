package com.dangdang.cymbal.service.cluster.service.entity;

import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.RedisMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ClusterEntityService}.
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterEntityServiceTest {

    @Resource
    ClusterEntityService clusterEntityService;

    @Test
    public void save() {
        Cluster cluster = buildRedisCluster();
        clusterEntityService.save(cluster);
        assertThat(cluster.getId()).isEqualTo(1);
    }

    @Test
    public void getById() {
        Cluster cluster = buildRedisCluster();
        clusterEntityService.save(cluster);
        cluster = clusterEntityService.getById(cluster.getId());
        assertThat(cluster.getId()).isEqualTo(1);
    }

    @Test
    public void getByClusterId() {
        Cluster cluster = buildRedisCluster();
        clusterEntityService.save(cluster);
        cluster = clusterEntityService.getByClusterId(cluster.getClusterId());
        assertThat(cluster.getId()).isEqualTo(1);
    }

    @Test
    public void list() {
        clusterEntityService.save(buildRedisCluster());
        clusterEntityService.save(buildRedisCluster());

        List<Cluster> clusters = clusterEntityService.list();
        assertThat(clusters.size()).isEqualTo(2);
    }


    private Cluster buildRedisCluster() {
        Cluster pojo = new Cluster();
        pojo.setCacheSize(1);
        pojo.setClusterId("Hr5bGKBQ");
        pojo.setRedisMode(RedisMode.CLUSTER);
        pojo.setPassword("b840fc02d524045429941cc15f59e41cb7be6c52");
        pojo.setDescription("test");
        pojo.setEnableSentinel(true);
        pojo.setEnv(Environment.TEST);
        pojo.setIdc(InternetDataCenter.TEST);
        pojo.setMasterCount(3);
        pojo.setRedisVersion("redis-3.2.11");
        pojo.setReplicaCount(1);
        pojo.setUserName("nobody");
        pojo.setUserCnName("无名氏");
        pojo.setStatus(ClusterStatus.UP);

        return pojo;
    }
}