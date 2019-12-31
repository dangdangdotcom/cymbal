package com.dangdang.cymbal.service.cluster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dangdang.cymbal.domain.po.Instance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Redis Server instance mapper.
 *
 * @auther GeZhen
 */
@Mapper
public interface InstanceMapper extends BaseMapper<Instance> {

    /**
     * Query the max port of redis server instance on one node.
     *
     * @param nodeId node id
     * @return max port
     */
    @Select("SELECT IFNULL(max(port), -1) FROM instance WHERE node_id = #{nodeId} AND type = 0")
    int queryMaxUsedPortOfNode(Integer nodeId);
}
