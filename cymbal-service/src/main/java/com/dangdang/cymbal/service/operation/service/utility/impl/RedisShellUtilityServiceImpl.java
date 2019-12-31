package com.dangdang.cymbal.service.operation.service.utility.impl;

import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.RedisUtil;
import com.dangdang.cymbal.service.util.enums.ShellCommand;
import com.dangdang.cymbal.service.util.service.impl.ShellService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @auther GeZhen
 */
@Slf4j
@Service
public class RedisShellUtilityServiceImpl implements RedisShellUtilityService {

    private final static String SENTINEL_MASTER_NAME_PREFIX = "mymaster-";

    @Resource
    private ShellService shellService;

    @Override
    public List<String> executeSentinelShellScript(final InstanceBO instanceBO, final ShellCommand shellCommand,
            final String... params) {
        Preconditions.checkNotNull(instanceBO, "Redis server instance is required.");
        return shellService.execSentinelShellScript((String[]) ArrayUtils
                .addAll(new String[]{instanceBO.getSelf().getRedisVersion(), getSentinelMasterName(instanceBO),
                        shellCommand.name().toLowerCase(), getRedisClusterPassword(instanceBO)}, params));
    }

    @Override
    public List<String> executeRedisShellScript(final InstanceBO instanceBO, final ShellCommand shellCommand,
            final String... params) {
        Preconditions.checkNotNull(instanceBO, "InstanceBO is required.");
        return shellService.execRedisShellScript((String[]) ArrayUtils
                .addAll(new String[]{instanceBO.getSelf().getRedisVersion(), shellCommand.name().toLowerCase(),
                        getRedisClusterPassword(instanceBO)}, params));
    }

    private String getSentinelMasterName(final InstanceBO instanceBO) {
        return SENTINEL_MASTER_NAME_PREFIX + instanceBO.getSelf().getClusterId();
    }

    private String getRedisClusterPassword(final InstanceBO instanceBO) {
        return RedisUtil.getRedisPassword(instanceBO.getPassword());
    }
}
