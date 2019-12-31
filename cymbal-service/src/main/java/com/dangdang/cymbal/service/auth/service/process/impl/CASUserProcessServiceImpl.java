package com.dangdang.cymbal.service.auth.service.process.impl;

import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.domain.po.User;

import java.util.List;

/**
 * @auther GeZhen
 */
public class CASUserProcessServiceImpl implements UserProcessService {

    @Override
    public String getUserCnName(final String userName) {
        return userName;
    }

    @Override
    public List<User> queryAllUsers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer createUser(final User user) {
        throw new UnsupportedOperationException();
    }
}
