package com.dangdang.cymbal.service.cluster.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for redis application form.
 *
 * @author GeZhen
 */
@Mapper
public interface ApplicationFormMapper extends BaseMapper<ApplicationForm> {
}
