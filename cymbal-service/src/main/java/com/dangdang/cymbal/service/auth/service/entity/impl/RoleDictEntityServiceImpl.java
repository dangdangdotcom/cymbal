package com.dangdang.cymbal.service.auth.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.service.auth.mapper.RoleDictMapper;
import com.dangdang.cymbal.service.auth.service.entity.RoleDictEntityService;
import com.dangdang.cymbal.domain.po.RoleDict;
import org.springframework.stereotype.Service;

/**
 * Implement for {@link RoleDictEntityService}.
 *
 * @auther Zhen.Ge
 */
@Service
public class RoleDictEntityServiceImpl extends ServiceImpl<RoleDictMapper, RoleDict> implements RoleDictEntityService {
}
