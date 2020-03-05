package com.dangdang.cymbal.service.auth.service.process.impl;

import com.dangdang.cymbal.domain.po.UserRole;
import com.dangdang.cymbal.service.auth.service.entity.UserRoleEntityService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRoleProcessServiceTest {

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Resource
    private UserRoleEntityService userRoleEntityService;

    @Test
    public void isAdmin() {
        buildUserRole();
        userRoleProcessService.isAdmin("Oasis");
    }

    private UserRole buildUserRole() {
        UserRole userRole = new UserRole();
        userRole.setRoleId(0);
        userRole.setUserEnName("Oasis");
        userRole.setCreationDate(new Date());
        userRole.setLastChangedDate(new Date());
        userRoleEntityService.save(userRole);
        return userRole;
    }
}