package com.dangdang.cymbal.service.operation.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.domain.po.ConfigDetail;

import java.util.List;

/**
 * Entity service for {@link ConfigDetail}.
 *
 * @auther GeZhen
 */
public interface ConfigEntityService extends IService<Config> {

    /**
     * Get redis server config by cluster id.
     *
     * @param clusterId cluster id
     * @return redis config
     */
    Config getByClusterIdOfRedis(String clusterId);

    /**
     * Query redis config by user name.
     *
     * @param userName user name
     * @return redis configs
     */
    List<Config> queryByUserName(String userName);
}
