package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Redis server instance model
 *
 * @author GeZhen
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Instance implements Serializable {

    private Integer id;

    @EqualsAndHashCode.Include
    private Integer nodeId;

    @EqualsAndHashCode.Include
    private Integer port;

    private String clusterId;

    private String redisVersion;

    private RedisReplicationRole role;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String slaveof;

    private String clusterNodeId;

    private InstanceStatus status;

    private InstanceType type;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
