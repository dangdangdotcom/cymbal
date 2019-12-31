package com.dangdang.cymbal.service.auth.service.entity;

import com.dangdang.cymbal.domain.po.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ClusterPermissionEntityService}
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRoleEntityServiceTest {

    @Resource
    UserRoleEntityService userRoleEntityService;

    @Test
    public void save() {
        UserRole userRole = buildUserRole();
        userRoleEntityService.save(userRole);

        assertThat(userRole.getId()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void getById() {
        UserRole userRole = buildUserRole();
        userRoleEntityService.save(userRole);

        userRole = userRoleEntityService.getById(userRole.getId());
        userRole = userRoleEntityService.getById(userRole.getId());
        assertThat(userRole).isNotNull();
    }

    @Test
    public void update() throws InterruptedException {
        UserRole userRole = buildUserRole();
        userRoleEntityService.save(userRole);

        Thread.sleep(1000);

        userRole.setRoleId(2);
        userRoleEntityService.updateById(userRole);

        UserRole userRoleUpdated = userRoleEntityService.getById(userRole.getId());

        assertThat(userRoleUpdated.getLastChangedDate().after(userRole.getLastChangedDate()));
    }

    public UserRole buildUserRole() {
        UserRole userRole = new UserRole();
        userRole.setRoleId(1);
        return userRole;
    }
}