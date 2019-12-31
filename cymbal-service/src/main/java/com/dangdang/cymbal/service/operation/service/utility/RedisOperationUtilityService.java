package com.dangdang.cymbal.service.operation.service.utility;

import com.dangdang.cymbal.domain.bo.InstanceBO;

import java.util.List;
import java.util.Set;

/**
 * For some complex operation of redis.
 *
 * @auther GeZhen
 */
public interface RedisOperationUtilityService {

    /**
     * Start up target redis server instances and change status.
     *
     * @param instanceBOs redis server instances to start
     */
    void startup(List<InstanceBO> instanceBOs);

    /**
     * Start up target redis server instance and change status.
     *
     * @param instanceIds redis server instance ids
     */
    void startup(Set<Integer> instanceIds);

    /**
     * Start up target redis server instance and change status.
     *
     * @param instanceBO redis server instance to start
     */
    void startup(InstanceBO instanceBO);

    /**
     * Shutdown redis server instances and change status.
     *
     *
     * @param instanceIds redis server instance ids
     */
    void shutdown(List<Integer> instanceIds);

    /**
     * Shutdown redis server instance and change status.
     *
     * @param instanceBO redis server instance BO
     */
    void shutdown(InstanceBO instanceBO);

    /**
     * Set slave of to a redis server instance and update instance entity.
     *
     * @param instanceId redis server instance id
     * @param newMasterHost master host(ip)
     * @param newMasterPort master port
     * @param newMasterPassword master password
     */
    void slaveOf(Integer instanceId, String newMasterHost, Integer newMasterPort, String newMasterPassword);

    /**
     * Set slave of to a redis server instance and update instance entity.
     *
     * @param instanceBO redis server instance BO
     * @param newMasterHost master host(ip)
     * @param newMasterPort master port
     * @param newMasterPassword master password
     */
    void slaveOf(InstanceBO instanceBO, String newMasterHost, Integer newMasterPort, String newMasterPassword);

    /**
     * Do failover to a slave instance of cluster and update instance entities, both master and slave.
     *
     * @param instanceId redis server instance id
     */
    void failover(Integer instanceId);

    /**
     * Do failover to a slave instance of cluster and update instance entities, both master and slave.
     *
     * @param instanceBO redis server instance BO
     */
    void failover(InstanceBO instanceBO);
}
