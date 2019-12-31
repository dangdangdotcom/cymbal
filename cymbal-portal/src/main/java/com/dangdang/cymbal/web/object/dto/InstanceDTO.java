package com.dangdang.cymbal.web.object.dto;

import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceStatus;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO of {@link Instance}.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class InstanceDTO {

    private Integer id;

    private Integer port;

    private String redisVersion;

    private RedisReplicationRole role;

    private String slaveof;

    private String clusterNodeId;

    private String clusterDescription;

    private InstanceStatus status;

    private InstanceType type;

    private Integer nodeId;

    private String ip;

    private String nodeDescription;

    private String clusterId;

    private Integer cacheSize;

    transient private String password;

    private String userName;

    private String userCnName;

    private Date creationDate;

    private Date lastChangedDate;
}
