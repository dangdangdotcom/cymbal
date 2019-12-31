package com.dangdang.cymbal.service.auth.service.process.impl;

import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.domain.po.User;
import com.dangdang.cymbal.service.auth.service.entity.UserEntityService;
import com.google.common.base.Preconditions;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * User service with DB.
 *
 * @auther GeZhen
 */
public class DbUserProcessServiceImpl implements UserProcessService {

    @Resource
    private UserEntityService userEntityService;

    @Override
    public String getUserCnName(String userName) {
        return userEntityService.getByUserName(userName).getUserCnName();
    }

    @Override
    public List<User> queryAllUsers() {
        return userEntityService.list();
    }

    @Override
    @Transactional
    public Integer createUser(final User user) {
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(user.getUserName());

        User oldUser = userEntityService.getByUserName(user.getUserName());
        Preconditions.checkState(Objects.isNull(oldUser));

        userEntityService.save(user);
        return user.getId();
    }
}
