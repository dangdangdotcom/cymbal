package com.dangdang.cymbal.web.object.dto;

import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.RedisMode;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for cluster import.
 *
 * @author GeZhen
 */
@Getter
@Setter
public class ImportClusterDTO {

    private Environment env;

    private InternetDataCenter idc;

    private String redisVersion;

    private String password;

    private RedisMode redisMode;

    private Integer cacheSize;

    private Integer masterCount;

    private Integer replicaCount;

    private String description;

    private String userName;

    private String userCnName;

    private String[] redisInstanceURIs;

    private String nodePassword;

    // private String[] sentinelInstanceURIs;
}
