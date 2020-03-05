package com.dangdang.cymbal.service.auth.service.process.impl;

import com.dangdang.cymbal.domain.po.RoleDict;
import com.dangdang.cymbal.domain.po.UserRole;
import com.dangdang.cymbal.service.auth.service.entity.RoleDictEntityService;
import com.dangdang.cymbal.service.auth.service.entity.UserRoleEntityService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Implement of {@link UserRoleProcessService}.
 *
 * @auther GeZhen
 */
@Service
public class UserRoleProcessServiceImpl implements UserRoleProcessService {

    private static final String ROLE_ADMIN = "system_administrator";

    @Resource
    private UserRoleEntityService userRoleEntityService;

    @Resource
    private RoleDictEntityService roleDictEntityService;

    @Override
    public boolean isAdmin(final String userName) {
        UserRole userRole = userRoleEntityService.getByUserName(userName);
        if (Objects.nonNull(userRole)) {
            RoleDict roleDict = roleDictEntityService.getById(userRole.getRoleId());
            if (Objects.nonNull(roleDict)) {
                return ROLE_ADMIN.equals(roleDict.getRoleName());
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Integer createUserRole(final UserRole userRole) {
        Preconditions.checkNotNull(userRole);
        Preconditions.checkNotNull(userRole.getUserEnName());

        UserRole oldUserRole = userRoleEntityService.getByUserName(userRole.getUserEnName());
        if (Objects.nonNull(oldUserRole)) {
            return oldUserRole.getId();
        }

        userRoleEntityService.save(userRole);
        return userRole.getId();
    }
}
