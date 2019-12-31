package com.dangdang.cymbal.service.operation.service.entity;

import com.dangdang.cymbal.domain.po.ConfigDetailStatus;
import com.dangdang.cymbal.domain.po.ConfigDetail;
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
public class ConfigDetailEntityServiceTest {

    @Resource
    ConfigDetailEntityService redisConfigDetailEntityService;

    @Test
    public void save() {
        ConfigDetail redisClusterConfigDetail = buildRedisClusterConfigDetail();
        redisConfigDetailEntityService.save(redisClusterConfigDetail);
        assertThat(redisClusterConfigDetail.getId()).isEqualTo(1);
    }

    public ConfigDetail buildRedisClusterConfigDetail() {
        ConfigDetail redisClusterConfigDetail = new ConfigDetail();
        redisClusterConfigDetail.setConfigId(1);
        redisClusterConfigDetail.setItemName("maxmemory-policy");
        redisClusterConfigDetail.setItemValue("volatile-lru");
        redisClusterConfigDetail.setStatus(ConfigDetailStatus.EFFECTIVE);
        redisClusterConfigDetail.setCreationDate(new Date());
        redisClusterConfigDetail.setLastChangedDate(new Date());
        return redisClusterConfigDetail;
    }
}
