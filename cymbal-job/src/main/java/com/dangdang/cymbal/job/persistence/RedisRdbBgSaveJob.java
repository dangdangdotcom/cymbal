package com.dangdang.cymbal.job.persistence;

import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.job.persistence.service.RedisPersistenceService;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.operation.enums.RedisConfigItem;
import com.dangdang.cymbal.service.operation.service.process.ConfigDetailProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Job for back ground save.
 *
 * @author GeZhen
 */
@Slf4j
@Component
public class RedisRdbBgSaveJob {

    @Resource
    private RedisPersistenceService redisPersistenceService;

    @Resource
    private ConfigDetailProcessService redisConfigDetailProcessService;

    @Resource
    private ClusterProcessService clusterProcessService;


    // TODO: Better way.
    // @Scheduled(fixedDelay = 60 * 1000)
    public void execute() {
        long startTime = System.currentTimeMillis();
        log.info("Redis bgsave job started.");
        for (ClusterBO each : clusterProcessService.queryAllRedisClusters()) {
            doBgSaveIfNeeded(each);
        }
        log.info("Redis bgsave job finished, duration time {} ms.", System.currentTimeMillis() - startTime);
    }

    private void doBgSaveIfNeeded(final ClusterBO redisClusterBO) {
        if (shouldBgSave(redisClusterBO.getCluster())) {
            log.debug("Start to bgsave for cluster {}.", redisClusterBO.getCluster().getClusterId());
            for (InstanceBO each : redisClusterBO.getInstanceBOs()) {
                Node node = each.getNode();
                log.debug("Start to bgsave for redis server {}:{}.", node.getIp(), each.getSelf().getPort());
                try {
                    redisPersistenceService.rdbBgSave(each);
                } catch (Exception e) {
                    log.error(e.getMessage(), "Fail to bgsave for redis server {}:{}.", node.getIp(),
                            each.getSelf().getPort());
                }
                log.debug("Bgsave for redis server {}:{} finished.", node.getIp(), each.getSelf().getPort());
            }
            log.debug("Bgsave for cluster {} finished.", redisClusterBO.getCluster().getClusterId());
        } else {
            log.debug("Redis cluster {} is enable persistence now, skip bgsave job.",
                    redisClusterBO.getCluster().getClusterId());
        }
    }

    private boolean shouldBgSave(final Cluster cluster) {
        boolean enablePersistence = false;
        boolean hasAofConfig = false;
        boolean hasSaveConfig = false;
        ConfigDetail rdbConfigDetail = redisConfigDetailProcessService
                .getByClusterIdAndItemName(cluster.getClusterId(), RedisConfigItem.SAVE.name().toLowerCase());
        if (Objects.nonNull(rdbConfigDetail)) {
            hasSaveConfig = true;
            if (!Constant.RedisConfig.SAVE_OFF.equals(rdbConfigDetail.getItemValue())) {
                enablePersistence = true;
            }
        }
        ConfigDetail aofConfigDetail = redisConfigDetailProcessService
                .getByClusterIdAndItemName(cluster.getClusterId(), RedisConfigItem.APPENDONLY.name().toLowerCase());
        if (Objects.nonNull(aofConfigDetail)) {
            hasAofConfig = true;
            if (!Constant.RedisConfig.APPENDONLY_NO.equals(rdbConfigDetail.getItemValue())) {
                enablePersistence = true;
            }
        }
        return hasSaveConfig && hasAofConfig && !enablePersistence;
    }
}
