package com.dangdang.cymbal.service.auth.service.entity.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.User;
import com.dangdang.cymbal.service.auth.mapper.UserMapper;
import com.dangdang.cymbal.service.auth.service.entity.UserEntityService;
import org.springframework.stereotype.Service;

/**
 * Implement of {@link UserEntityService}.
 *
 * @auther GeZhen
 */
@Service
public class UserEntityServiceImpl extends ServiceImpl<UserMapper, User> implements UserEntityService {

    @Override
    public User getByUserName(final String userName) {
        return this.lambdaQuery().eq(User::getUserName, userName).one();
    }
}
