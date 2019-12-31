package com.dangdang.cymbal.service.operation.service.utility;

import com.dangdang.cymbal.service.util.enums.ShellCommand;
import com.dangdang.cymbal.domain.bo.InstanceBO;

import java.util.List;

/**
 * Method about execute shell scripts for redis.
 *
 * @auther GeZhen
 */
public interface RedisShellUtilityService {

    /**
     * Execute shell script for sentinel.
     *
     * @param instance redis server instance
     * @param params shell script params
     * @return result of shell execution
     */
    List<String> executeSentinelShellScript(InstanceBO instance, ShellCommand shellCommand, String... params);

    /**
     * Execute shell script for redis.
     *
     * @param instance redis server instance
     * @param params shell script params
     * @return result of shell execution
     */
    List<String> executeRedisShellScript(InstanceBO instance, ShellCommand shellCommand, String... params);
}
