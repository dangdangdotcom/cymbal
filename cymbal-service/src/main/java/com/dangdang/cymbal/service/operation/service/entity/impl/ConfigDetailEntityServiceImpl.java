package com.dangdang.cymbal.service.operation.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.service.operation.mapper.ConfigDetailMapper;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDetailEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implement for {@link ConfigDetailEntityService}.
 *
 * @auther GeZhen
 */
@Service
public class ConfigDetailEntityServiceImpl extends ServiceImpl<ConfigDetailMapper, ConfigDetail>
        implements ConfigDetailEntityService {

    @Override
    public ConfigDetail getByConfigIdAndItemName(final Integer configId, final String itemName) {
        return this.lambdaQuery().eq(ConfigDetail::getConfigId, configId).eq(ConfigDetail::getItemName, itemName).one();
    }

    @Override
    public List<ConfigDetail> queryByConfigId(final Integer configId) {
        return this.lambdaQuery().eq(ConfigDetail::getConfigId, configId).list();
    }
}
