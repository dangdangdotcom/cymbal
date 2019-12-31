package com.dangdang.cymbal.job.persistence.service;

import com.dangdang.cymbal.domain.bo.InstanceBO;

/**
 * @author GeZhen
 */
public interface RedisPersistenceService {

    /**
     * Do background save to a instance in sync way.
     * Method will return when background save is done.
     *
     * @param instanceBO instance to do background save
     */
    void rdbBgSave(InstanceBO instanceBO);

    /**
     * Do background save to a instance in async way.
     *
     * @param instanceBO instance to do background save
     */
    void rdbBgSaveAsync(InstanceBO instanceBO);
}
