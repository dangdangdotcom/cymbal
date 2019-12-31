package com.dangdang.cymbal.service.auth.service.process;

import com.dangdang.cymbal.domain.po.UserRole;

/**
 * Process service for {@link UserRole}.
 *
 * @auther Zhen.Ge
 */
public interface UserRoleProcessService {

    /**
     * Check target user is admin or not.
     *
     * @param userName user name
     * @return true if the user is admin
     */
    boolean isAdmin(String userName);

    /**
     * Create a role of admin for target user, if not existed.
     *
     * @param userRole user role
     */
    Integer createUserRole(UserRole userRole);
}
