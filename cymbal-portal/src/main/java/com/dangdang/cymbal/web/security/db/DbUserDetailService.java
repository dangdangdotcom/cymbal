package com.dangdang.cymbal.web.security.db;

import com.dangdang.cymbal.web.security.enums.UserRole;
import com.dangdang.cymbal.domain.po.User;
import com.dangdang.cymbal.service.auth.service.entity.UserEntityService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * UserDetailService for DB.
 *
 * @auther GeZhen
 */
public class DbUserDetailService implements UserDetailsService {

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Resource
    private UserEntityService userEntityService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userEntityService.getByUserName(userName);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(String.format("Can not find a user with name '%s'.", userName));
        }
        if (userRoleProcessService.isAdmin(userName)) {
            return new org.springframework.security.core.userdetails.User(userName, user.getPassword(),
                    AuthorityUtils.createAuthorityList(UserRole.ADMIN.getValue()));
        } else {
            return new org.springframework.security.core.userdetails.User(userName, user.getPassword(),
                    AuthorityUtils.NO_AUTHORITIES);
        }
    }
}
