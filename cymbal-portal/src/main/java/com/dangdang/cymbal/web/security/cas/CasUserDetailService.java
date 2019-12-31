package com.dangdang.cymbal.web.security.cas;

import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.web.security.enums.UserRole;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;

/**
 * UserDetailService for CAS.
 *
 * @author GeZhen
 */
public class CasUserDetailService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        String userName = token.getName();
        if (userRoleProcessService.isAdmin(userName)) {
            return new User(userName, Constant.Strings.EMPTY,
                    AuthorityUtils.createAuthorityList(UserRole.ADMIN.getValue()));
        } else {
            return new User(userName, Constant.Strings.EMPTY, AuthorityUtils.NO_AUTHORITIES);
        }
    }
}
