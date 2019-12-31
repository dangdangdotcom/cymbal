package com.dangdang.cymbal.web.object.dto;

import com.dangdang.cymbal.domain.po.AlarmLevel;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.RedisMode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO of {@link Cluster}.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class ClusterDTO {

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

    private Date creationDate;
}
