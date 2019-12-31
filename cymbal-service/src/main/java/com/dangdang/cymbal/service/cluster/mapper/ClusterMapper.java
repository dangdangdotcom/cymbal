package com.dangdang.cymbal.service.cluster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dangdang.cymbal.domain.po.Cluster;
import org.apache.ibatis.annotations.Mapper;

/**
 * Redis cluster mapper.
 *
 * @auther GeZhen
 */
@Mapper
public interface ClusterMapper extends BaseMapper<Cluster> {
}
