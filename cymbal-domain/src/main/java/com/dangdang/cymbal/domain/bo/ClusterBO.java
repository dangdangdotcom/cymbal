package com.dangdang.cymbal.domain.bo;

import com.dangdang.cymbal.domain.po.Cluster;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * BO of redis cluster.
 *
 * @auther GeZhen
 */
@Getter
@Setter
@ToString
@Builder
public class ClusterBO {

    private Cluster cluster;

    private List<InstanceBO> instanceBOs;
}
