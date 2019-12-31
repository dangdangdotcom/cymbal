package com.dangdang.cymbal.service.operation.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.operation.mapper.ConfigMapper;
import com.dangdang.cymbal.service.operation.service.entity.ConfigEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implement of {@link ConfigEntityService}.
 *
 * @auther GeZhen
 */
@Service
public class ConfigEntityServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigEntityService {

    @Override
    public Config getByClusterIdOfRedis(final String clusterId) {
        return this.lambdaQuery().eq(Config::getClusterId, clusterId)
                .ne(Config::getRedisVersion, Constant.RedisConfig.SENTINEL_CONFIG_VERSION).one();
    }

    @Override
    public List<Config> queryByUserName(final String userName) {
        return this.lambdaQuery().eq(Config::getUserName, userName).list();
    }
}
