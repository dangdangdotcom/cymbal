package com.dangdang.cymbal.service.operation.service.entity;

import com.dangdang.cymbal.domain.po.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigEntityServiceTest {

    @Resource
    ConfigEntityService redisConfigEntityService;

    @Test
    public void save() {
        Config redisClusterConfig = buildRedisClusterConfig();
        redisConfigEntityService.save(redisClusterConfig);
        assertThat(redisClusterConfig.getId()).isEqualTo(1);
    }

    public Config buildRedisClusterConfig() {
        Config redisClusterConfig = new Config();
        redisClusterConfig.setClusterId("12345678");
        redisClusterConfig.setConfigName("test-redis-cluster");
        redisClusterConfig.setRedisVersion("redis-3.2.11");
        redisClusterConfig.setUserName("ACDC");
        redisClusterConfig.setUserCnName("埃塞蒂塞");
        redisClusterConfig.setCreationDate(new Date());
        redisClusterConfig.setLastChangedDate(new Date());
        return redisClusterConfig;
    }
}