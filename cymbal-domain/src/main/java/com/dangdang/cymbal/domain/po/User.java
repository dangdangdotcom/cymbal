package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * User model.
 *
 * @auther GeZhen
 */
@Getter
@Setter
@ToString
public class User {

    private Integer id;

    private String userName;

    private String userCnName;

    private String email;

    @TableField()
    private String password;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
