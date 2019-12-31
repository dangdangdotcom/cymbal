package com.dangdang.cymbal.service.auth.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.User;

/**
 * Entity service for {@link User}.
 *
 * @auther GeZhen
 */
public interface UserEntityService extends IService<User> {

    /**
     * Get user by user name.
     *
     * @param userName user name
     * @return user eneity
     */
    User getByUserName(String userName);
}
