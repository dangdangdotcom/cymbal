package com.dangdang.cymbal.service.operation.service.utility;

import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.service.operation.enums.RedisReplyFormat;

import java.util.List;
import java.util.Map;

/**
 * Service for redis client.
 *
 * @auther GeZhen
 */
public interface RedisClientUtilityService {

    /**
     * Start up target redis server instance.
     *
     * @param instanceBO redis server instance to start
     */
    void startup(InstanceBO instanceBO);

    /**
     * Set slave of to a redis server instance.
     *
     * @param instanceBO redis server instance BO
     * @param newMasterHost new master host
     * @param newMasterPort new master port
     * @param newMasterPassword new master password
     */
    void slaveOf(InstanceBO instanceBO, String newMasterHost, String newMasterPort, String newMasterPassword);


    /**
     * Send cluster nodes command to a redis server instance.
     *
     * @param instanceBO redis server instance BO
     */
    List<ClusterNodeBO> clusterNodes(InstanceBO instanceBO);

    /**
     * Execute a redis command and return result.
     *
     * @param instanceBO redis server instance BO
     * @param command command
     * @param redisReplyFormat reply format
     * @return execution result
     */
    List<String> executeRedisCommand(InstanceBO instanceBO, String command, RedisReplyFormat redisReplyFormat);

    /**
     * Execute a redis command.
     *
     * @param instanceBO redis server instance BO
     * @param command command
     */
    void executeRedisCommand(final InstanceBO instanceBO, final String command);

    /**
     * Execute a redis command and return result.
     *
     * @param instanceId redis server instance id
     * @param command command
     * @param redisReplyFormat reply format
     * @return execution result
     */
    List<String> executeRedisCommand(Integer instanceId, String command, RedisReplyFormat redisReplyFormat);

    /**
     * Execute a config set command to redis.
     *
     * @param instanceBO target redis server instance
     * @param configItemName config item name
     * @param configItemValue config item value
     */
    void configSet(InstanceBO instanceBO, String configItemName, String configItemValue);

    /**
     * Shut down redis server instance.
     *
     * @param instanceBO redis server instance BO
     */
    void shutdown(InstanceBO instanceBO);

    /**
     * Execute bgsave command to redis server instance.
     *
     * @param instanceBO redis server instance BO
     */
    void bgsave(InstanceBO instanceBO);

    /**
     * Execute info persistence command to redis server instance.
     *
     * @param instanceBO redis server instance BO
     */
    List<String> infoPersistence(InstanceBO instanceBO);

    /**
     * Do failover to a slave instance of cluster.
     *
     * @param instanceBO redis server instance BO
     */
    void failover(InstanceBO instanceBO);

    /**
     * Scrape monitor info of a instance.
     *
     * @param instanceBO instance BO
     * @return monitor info
     */
    Map<String, String> scrapeMonitorInfo(InstanceBO instanceBO);
}
