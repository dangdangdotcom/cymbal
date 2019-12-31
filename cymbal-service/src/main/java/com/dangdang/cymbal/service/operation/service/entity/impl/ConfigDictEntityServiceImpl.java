package com.dangdang.cymbal.service.operation.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.ConfigDict;
import com.dangdang.cymbal.service.operation.mapper.ConfigDictMapper;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDictEntityService;
import org.springframework.stereotype.Service;

/**
 * @auther GeZhen
 */
@Service
public class ConfigDictEntityServiceImpl extends ServiceImpl<ConfigDictMapper, ConfigDict>
        implements ConfigDictEntityService {
}
