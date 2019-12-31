package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RoleDict {

    private Integer id;

    private String roleName;

    private Date creationDate;

    @TableField(update = "now()")
    private Date lastChangedDate;
}
