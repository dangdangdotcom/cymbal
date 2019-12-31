package com.dangdang.cymbal.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dangdang.cymbal.domain.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link User}.
 *
 * @auther GeZhen
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
