package com.dangdang.cymbal.job.replication;

import com.dangdang.cymbal.common.exception.CymbalException;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.AlarmLevel;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.service.operation.service.utility.RedisReplicationUtilityService;
import com.dangdang.cymbal.service.util.MailUtil;
import com.dangdang.cymbal.service.util.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Job to check redis clusters replication is healthy.
 *
 * @author GeZhen
 */
@Slf4j
@Component
public class RedisClusterReplicationCheckJob {

    @Resource
    private ClusterProcessService clusterProcessService;

    @Resource
    private RedisReplicationUtilityService redisReplicationUtilityService;

    @Resource
    private MailService mailService;

    @Scheduled(cron = "${job.replication.cron}")
    public void execute() {
        long startTime = System.currentTimeMillis();
        log.info("RedisClusterReplicationCheckJob started.");
        // TODO: Query with page
        List<ClusterBO> redisClusterBOs = clusterProcessService.queryAllRedisClusters();
        for (ClusterBO eachRedisClusterBO : redisClusterBOs) {
            try {
                if (AlarmLevel.LOG.equals(eachRedisClusterBO.getCluster().getAlarmLevel())) {
                    log.warn("Cluster of id '{}' is sign to skip alarm, skip check.",
                            eachRedisClusterBO.getCluster().getClusterId());
                    continue;
                }
                doAlarmIfNeeded(eachRedisClusterBO);
            } catch (CymbalException e) {
                log.error(String.format("RedisMasterDistributionCheckJob error of clusterId %s",
                        eachRedisClusterBO.getCluster().getClusterId()), e);
            }
        }

        log.info("RedisClusterReplicationCheckJob finished, duration time {} ms.",
                System.currentTimeMillis() - startTime);
    }

    private void doAlarmIfNeeded(final ClusterBO redisClusterBO) {
        redisReplicationUtilityService.refreshReplication(redisClusterBO.getInstanceBOs());
        Map<Node, List<InstanceBO>> nodeRedisServerInstancesMap = redisClusterBO.getInstanceBOs().stream()
                .collect(Collectors.groupingBy(InstanceBO::getNode, Collectors.toList()));
        for (Map.Entry<Node, List<InstanceBO>> each : nodeRedisServerInstancesMap.entrySet()) {
            List<InstanceBO> instanceBOS = each.getValue();
            if (instanceBOS.size() == 1) {
                continue;
            } else {
                int balance = 0;
                for (InstanceBO eachInstanceBO : instanceBOS) {
                    switch (eachInstanceBO.getSelf().getRole()) {
                        case MASTER:
                            balance++;
                            break;
                        case SLAVE:
                            balance--;
                            break;
                        default:
                            break;
                    }
                }
                if (balance > 0) {
                    sendAlarmMail(redisClusterBO.getCluster(), each.getKey().getIp());
                    break;
                }
            }
        }
    }

    private void sendAlarmMail(final Cluster cluster, final String nodeIp) {
        try {
            String title = String.format("'%s'主机主从节点分配不均，请尽快调整", nodeIp);
            // TODO: Try a better way.
            String content = MailUtil.getClusterInfoForHtmlMail(cluster);
            mailService.sendHtmlMailToAdmin(title, content);
        } catch (CymbalException e) {
            log.error("Fail to send mail.", e);
        }
    }
}
