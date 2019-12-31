package com.dangdang.cymbal.service.auth.service.entity;

import com.dangdang.cymbal.domain.po.ClusterPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ClusterPermissionEntityService}
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterPermissionEntityServiceTest {

    @Resource
    ClusterPermissionEntityService clusterPermissionEntityService;

    @Test
    public void save() {
        ClusterPermission clusterPermission = buildRedisClusterPermission();
        clusterPermissionEntityService.save(clusterPermission);
        assertThat(clusterPermission.getId()).isEqualTo(1);
    }

    private ClusterPermission buildRedisClusterPermission() {
        ClusterPermission clusterPermission = new ClusterPermission();
        clusterPermission.setClusterId("test");
        clusterPermission.setUserName("nobody");
        clusterPermission.setUserCnName("无名氏");
        return clusterPermission;
    }
}