package com.dangdang.cymbal.service.auth.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.UserRole;

/**
 * Entity service for user role.
 *
 * @auther GeZhen
 */
public interface UserRoleEntityService extends IService<UserRole> {

    /**
     * Get UserRole by user name.
     *
     * @param userName user name
     * @return user role
     */
    UserRole getByUserName(String userName);
}
