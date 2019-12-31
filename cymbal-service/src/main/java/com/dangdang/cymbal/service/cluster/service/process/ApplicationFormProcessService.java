package com.dangdang.cymbal.service.cluster.service.process;

import com.dangdang.cymbal.domain.po.ApplicationForm;

/**
 * Process service for RedisApplicationForm.
 *
 * @auther GeZhen
 */
public interface ApplicationFormProcessService {

    /**
     * Create a redis application form.
     * Include insert entity and send mail to admin user, and so on.
     *
     * @param applicationForm redis application form to insert
     * @return id of the new entity
     */
    Integer saveRedisApplicationForm(ApplicationForm applicationForm);

    /**
     * Update a redis application form.
     *
     * @param applicationForm
     */
    void updateRedisApplicationForm(ApplicationForm applicationForm);

    /**
     * Deny a redis application form.
     * Include update entity and send mail to admin user, and so on.
     *
     * @param applicationFormId redis application id
     * @param approvalOpinion approval opinion
     */
    void denyRedisApplicationForm(Integer applicationFormId, String approvalOpinion);

    /**
     * Approve a redis application form and do the alloc job.
     * Such as build a redis cluster, monitor and send mail to applicant.
     *
     * @param applicationForm redis application form
     * @return created cluster id
     */
    String approveRedisApplicationForm(ApplicationForm applicationForm);
}
