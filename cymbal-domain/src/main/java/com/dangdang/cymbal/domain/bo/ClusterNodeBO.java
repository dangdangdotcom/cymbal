package com.dangdang.cymbal.domain.bo;

import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * BO of cluster node.
 *
 * @auther GeZhen
 */
@Getter
@Setter
@ToString
@Builder
public class ClusterNodeBO {

    private String clusterNodeId;

    private String ip;

    private Integer port;

    private RedisReplicationRole role;

    private String masterClusterNodeId;
}
