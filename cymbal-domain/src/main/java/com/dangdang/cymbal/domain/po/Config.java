package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Config {

    private Integer id;

    private String configName;

    private String userName;

    private String userCnName;

    private String clusterId;

    private String redisVersion;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
