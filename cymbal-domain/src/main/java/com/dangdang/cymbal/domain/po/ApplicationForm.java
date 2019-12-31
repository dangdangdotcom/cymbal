package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Model for redis application form.
 *
 * @author GeZhen
 */
@Getter
@Setter
@ToString
public class ApplicationForm {

    private Integer id;

    private String applicantEnName;

    private String applicantCnName;

    private Environment env;

    private InternetDataCenter idc;

    private RedisMode redisMode;

    private String redisVersion;

    private Integer cacheSize;

    private Integer masterCount;

    private Integer replicaCount;

    private RedisPersistenceType redisPersistenceType;

    private boolean enableSentinel;

    private String password;

    private String belongSystem;

    private String description;

    private ApplicationFormStatus status;

    private String approvalOpinion;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
