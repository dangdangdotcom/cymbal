package com.dangdang.cymbal.service.cluster.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Entity service for RedisApplicationForm.
 *
 * @author GeZhen
 */
public interface ApplicationFormEntityService extends IService<ApplicationForm> {

    /**
     * Query by status with page.
     *
     * @param status application form status
     * @param pageable pageable
     * @return page of RedisApplicationForm
     */
    Page<ApplicationForm> queryByStatusWithPage(ApplicationFormStatus status, Pageable pageable);

    /**
     * Query by user name with page.
     *
     * @param userName user name
     * @param pageable pageable
     * @return page of RedisApplicationForm
     */
    Page<ApplicationForm> queryByUserNameWithPage(String userName, Pageable pageable);
}
