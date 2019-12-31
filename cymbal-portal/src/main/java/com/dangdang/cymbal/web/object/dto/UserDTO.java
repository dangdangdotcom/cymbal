package com.dangdang.cymbal.web.object.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO of {@link com.dangdang.cymbal.domain.po.User}.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class UserDTO {

    private Integer id;

    private String userName;

    private String userCnName;

    private String email;

    private String password;
}
