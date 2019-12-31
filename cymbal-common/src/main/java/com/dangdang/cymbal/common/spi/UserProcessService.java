package com.dangdang.cymbal.common.spi;


import com.dangdang.cymbal.domain.po.User;

import java.util.List;

/**
 * User process service.
 * May implement in many way.
 * Such as CAS, LDAP, DB, and so on.
 *
 * @auther GeZhen
 */
public interface UserProcessService {

    /**
     * Get user chinese name.
     *
     * @param userName user english name (user id)
     * @return user chinese name
     */
    String getUserCnName(String userName);

    /**
     * Query all users.
     *
     * @return all users
     */
    List<User> queryAllUsers();

    /**
     * Create a user.
     *
     * @param user
     * @return
     */
    Integer createUser(User user);
}
