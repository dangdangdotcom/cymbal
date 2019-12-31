package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Redis cluster model..
 *
 * @author GeZhen
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Cluster implements Serializable {

    private Integer id;

    @EqualsAndHashCode.Include
    private String clusterId;

    private String userName;

    private String userCnName;

    private Environment env;

    private InternetDataCenter idc;

    private Integer masterCount;

    private Integer replicaCount;

    private int cacheSize;

    private ClusterStatus status;

    private RedisMode redisMode;

    private String redisVersion;

    private boolean enableSentinel;

    private String password;

    private String description;

    private AlarmLevel alarmLevel;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
