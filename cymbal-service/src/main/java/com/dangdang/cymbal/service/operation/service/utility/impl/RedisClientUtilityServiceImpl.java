package com.dangdang.cymbal.service.operation.service.utility.impl;

import com.dangdang.cymbal.common.util.CollectionUtil;
import com.dangdang.cymbal.domain.bo.ClusterNodeBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.domain.po.InstanceType;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.operation.enums.RedisCommand;
import com.dangdang.cymbal.service.operation.enums.RedisReplyFormat;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisShellUtilityService;
import com.dangdang.cymbal.service.util.RedisUtil;
import com.dangdang.cymbal.service.util.enums.ShellCommand;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implement of {@link RedisClientUtilityService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class RedisClientUtilityServiceImpl implements RedisClientUtilityService {

    private final static int REDIS_CLUSTER_NODE_ID_LENGTH = 40;

    @Resource
    private RedisShellUtilityService redisShellUtilityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Override
    public void startup(final InstanceBO instanceBO) {
        if (InstanceType.SENTINEL.equals(instanceBO.getSelf().getType())) {
            redisShellUtilityService
                    .executeSentinelShellScript(instanceBO, ShellCommand.START, instanceBO.getNode().getIp(),
                            instanceBO.getSelf().getPort().toString());
        } else {
            redisShellUtilityService
                    .executeRedisShellScript(instanceBO, ShellCommand.START, instanceBO.getNode().getIp(),
                            instanceBO.getSelf().getPort().toString());
        }
    }

    @Override
    public void slaveOf(final InstanceBO instanceBO, final String newMasterHost, final String newMasterPort,
            final String newMasterPassword) {
        Instance instance = instanceBO.getSelf();
        Node node = instanceBO.getNode();
        List<String> result = redisShellUtilityService
                .executeRedisShellScript(instanceBO, ShellCommand.SLAVEOF, instance.getClusterId(), node.getIp(),
                        instance.getPort().toString(), newMasterHost, newMasterPort,
                        RedisUtil.getRedisPassword(newMasterPassword));
        checkShellExecuteResult(result);
    }

    private void checkShellExecuteResult(final List<String> result) {
        Preconditions.checkArgument(
                result != null && result.size() == 2 && Constant.Redis.EXECUTE_RESULT_SUCCESS.equals(result.get(0))
                        && Constant.Redis.EXECUTE_RESULT_SUCCESS.equals(result.get(1)),
                "Shell execute fail, result is %s.", result);
    }

    @Override
    public List<ClusterNodeBO> clusterNodes(final InstanceBO instanceBO) {
        List<String> result = this
                .executeRedisCommand(instanceBO, RedisCommand.CLUSTER_NODES.getValue(), RedisReplyFormat.RAW);
        Preconditions.checkState(!result.isEmpty(),
                String.format("Can not query cluster nodes from redis server instance '%s:%d'.",
                        instanceBO.getNode().getIp(), instanceBO.getSelf().getPort()));
        return transferToClusterNodeBOs(result);
    }

    @Override
    public List<String> executeRedisCommand(final InstanceBO instanceBO, final String command,
            final RedisReplyFormat redisReplyFormat) {
        String commandWithSingleQuotes = String.format("'%s'", command);
        return redisShellUtilityService
                .executeRedisShellScript(instanceBO, ShellCommand.REDIS_CLI_CMD, instanceBO.getNode().getIp(),
                        instanceBO.getSelf().getPort().toString(), commandWithSingleQuotes,
                        redisReplyFormat.getValue());
    }

    @Override
    public void executeRedisCommand(final InstanceBO instanceBO, final String command) {
        this.executeRedisCommand(instanceBO, command, RedisReplyFormat.RAW);
    }

    @Override
    public List<String> executeRedisCommand(final Integer instanceId, final String command,
            final RedisReplyFormat redisReplyFormat) {
        InstanceBO instanceBO = instanceProcessService.getInstanceBOById(instanceId);
        return this.executeRedisCommand(instanceBO, command, redisReplyFormat);
    }

    private List<ClusterNodeBO> transferToClusterNodeBOs(final List<String> commandResult) {
        List<ClusterNodeBO> clusterNodeBOs = new ArrayList<>();
        commandResult.forEach(each -> {
            String[] words = each.split(Constant.Strings.BLANK_SPACE);
            Preconditions.checkState(words[0].length() == REDIS_CLUSTER_NODE_ID_LENGTH,
                    "Can not transfer '%s' to ClusterNodeBO.", each);

            ClusterNodeBO clusterNodeBO = ClusterNodeBO.builder().clusterNodeId(words[0]).ip(words[1].split(":")[0])
                    .port(Integer.valueOf(words[1].split(":")[1].split("@")[0])).build();
            if (words[2].indexOf(",") > 0) {
                clusterNodeBO.setRole(
                        RedisReplicationRole.valueOf(words[2].substring(words[2].indexOf(",") + 1).toUpperCase()));
            } else {
                clusterNodeBO.setRole(RedisReplicationRole.valueOf(words[2].toUpperCase()));
            }
            if (words[3].length() == REDIS_CLUSTER_NODE_ID_LENGTH) {
                clusterNodeBO.setMasterClusterNodeId(words[3]);
            }
            clusterNodeBOs.add(clusterNodeBO);
        });
        return clusterNodeBOs;
    }

    @Override
    public void configSet(final InstanceBO instanceBO, final String configItemName, final String configItemValue) {
        Instance instance = instanceBO.getSelf();
        Node node = instanceBO.getNode();
        if (InstanceType.SENTINEL.equals(instanceBO.getSelf().getType())) {
            redisShellUtilityService.executeSentinelShellScript(instanceBO, ShellCommand.CONFIG, node.getIp(),
                    instance.getPort().toString(), instance.getClusterId(), configItemName, configItemValue);
        } else {
            redisShellUtilityService.executeRedisShellScript(instanceBO, ShellCommand.CONFIG, node.getIp(),
                    instance.getPort().toString(), instance.getClusterId(), configItemName, configItemValue);
        }
    }

    @Override
    public void shutdown(final InstanceBO instanceBO) {
        if (InstanceType.SENTINEL.equals(instanceBO.getSelf().getType())) {
            redisShellUtilityService
                    .executeSentinelShellScript(instanceBO, ShellCommand.STOP, instanceBO.getNode().getIp(),
                            instanceBO.getSelf().getPort().toString());
        } else {
            redisShellUtilityService
                    .executeRedisShellScript(instanceBO, ShellCommand.STOP, instanceBO.getNode().getIp(),
                            instanceBO.getSelf().getPort().toString());
        }
    }

    @Override
    public void bgsave(final InstanceBO instanceBO) {
        this.executeRedisCommand(instanceBO, RedisCommand.BGSAVE.getValue(), RedisReplyFormat.RAW);
    }

    @Override
    public List<String> infoPersistence(final InstanceBO instanceBO) {
        return this.executeRedisCommand(instanceBO, RedisCommand.INFO_PERSISTENCE.getValue(), RedisReplyFormat.RAW);
    }

    @Override
    public void failover(final InstanceBO instanceBO) {
        this.executeRedisCommand(instanceBO, RedisCommand.CLUSTER_FAILOVER.getValue(), RedisReplyFormat.RAW);
    }

    @Override
    public Map<String, String> scrapeMonitorInfo(final InstanceBO instanceBO) {
        List<String> shellResult = redisShellUtilityService
                .executeRedisShellScript(instanceBO, ShellCommand.MONITOR, instanceBO.getNode().getIp(),
                        instanceBO.getSelf().getPort().toString());
        return CollectionUtil.splitByColonAndToMap(shellResult);
    }
}
