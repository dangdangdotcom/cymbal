package com.dangdang.cymbal.service.auth.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.UserRole;
import com.dangdang.cymbal.service.auth.mapper.UserRoleMapper;
import com.dangdang.cymbal.service.auth.service.entity.UserRoleEntityService;
import org.springframework.stereotype.Service;

/**
 * Implement for UserRoleEntityService.
 *
 * @auther GeZhen
 */
@Service
public class UserRoleEntityServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleEntityService {

    @Override
    public UserRole getByUserName(final String userName) {
        return this.lambdaQuery().eq(UserRole::getUserEnName, userName).one();
    }
}
