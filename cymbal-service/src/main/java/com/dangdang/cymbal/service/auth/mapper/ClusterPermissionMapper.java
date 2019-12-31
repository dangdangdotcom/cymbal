package com.dangdang.cymbal.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dangdang.cymbal.domain.po.ClusterPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for redis cluster permission.
 *
 * @auther GeZhen
 */
@Mapper
public interface ClusterPermissionMapper extends BaseMapper<ClusterPermission> {
}
