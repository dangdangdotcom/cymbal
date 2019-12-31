package com.dangdang.cymbal.service.operation.service.process;

import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.service.operation.service.entity.ConfigEntityService;
import com.dangdang.cymbal.service.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ConfigProcessService}.
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigProcessServiceTest {

    @Resource
    private ConfigEntityService redisConfigEntityService;

    @Resource
    private ConfigProcessService redisConfigProcessService;

    @Test
    public void createConfigForNewRedisCluster() {
    }

    @Test
    public void createConfigForImportedRedisCluster() {
    }

    @Test
    public void effectConfigForScaledInstances() {
    }

    @Test
    public void queryByUserName() {
    }

    @Test
    @Transactional
    public void updateConfigName() {
        Config config = buildRedisConfig();
        String newName = "new name";
        redisConfigProcessService.updateConfigName(config.getId(), newName);
        Config updatedConfig = redisConfigEntityService.getById(config.getId());
        assertThat(updatedConfig.getConfigName()).isEqualTo(newName);
    }

    @Test(expected = NullPointerException.class)
    public void updateConfigNameWhenNotExist() {
        redisConfigProcessService.updateConfigName(Integer.MAX_VALUE, "changed config name");
    }

    private Config buildRedisConfig() {
        Config config = new Config();
        config.setConfigName("Jeff Beck 's cluster config");
        config.setClusterId(RedisUtil.generateClusterId());
        config.setCreationDate(new Date());
        config.setLastChangedDate(config.getCreationDate());
        config.setRedisVersion("redis-4.0.12");
        config.setUserName("Jeff Beck");
        config.setUserCnName("杰夫·贝克");
        redisConfigEntityService.save(config);
        return config;
    }
}
