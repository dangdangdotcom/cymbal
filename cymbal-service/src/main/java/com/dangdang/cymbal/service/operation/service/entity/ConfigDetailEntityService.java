package com.dangdang.cymbal.service.operation.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.ConfigDetail;

import java.util.List;

/**
 * Entity service for {@link ConfigDetail}.
 *
 * @auther GeZhen
 */
public interface ConfigDetailEntityService extends IService<ConfigDetail> {

    /**
     * Get a config detail by given config id and item name.
     *
     * @param configId parent config id
     * @param itemName item name
     * @return redis config detail
     */
    ConfigDetail getByConfigIdAndItemName(Integer configId, String itemName);

    /**
     * Query redis config details by redis config id.
     *
     * @param configId redis config id
     * @return redis config details
     */
    List<ConfigDetail> queryByConfigId(Integer configId);
}
