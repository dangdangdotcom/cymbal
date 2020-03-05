package com.dangdang.cymbal.service.auth.service.entity;

import com.dangdang.cymbal.domain.po.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserEntityServiceTest {

    @Resource
    UserEntityService userEntityService;

    @Test
    public void save() {
        User user = buildUser();
        userEntityService.save(user);
        assertThat(user.getId()).isNotNull();
    }

    public User buildUser() {
        User user = new User();
        user.setUserName("Metallica");
        user.setUserCnName("米太利卡");
        user.setPassword("password");
        return user;
    }
}