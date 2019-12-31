package com.dangdang.cymbal.service.monitor.service.impl;

import com.dangdang.cymbal.common.enums.CapacityUnit;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.monitor.bean.RedisMonitorInfo;
import com.dangdang.cymbal.service.monitor.enums.MonitorType;
import com.dangdang.cymbal.service.monitor.service.MetricCollectionService;
import com.dangdang.cymbal.service.monitor.service.MonitorService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Entrance of monitor service.
 * Hide the detail of monitor implements.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {


    // TODO: All about this property need change to a better way.
    @Value("${monitor.type}")
    private MonitorType monitorType;

    @Resource
    private MetricCollectionService metricCollectionService;

    @Resource
    private ClusterProcessService clusterProcessService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Override
    public void initMonitorForNodes(final List<Node> nodes) {
        if (monitorType == MonitorType.GRAFANA) {
            metricCollectionService.addNodes(nodes);
        }
    }

    @Override
    public void initMonitorForCluster(final ClusterBO redisClusterBO) {
        this.initMonitorForInstances(redisClusterBO.getInstanceBOs());
    }

    @Override
    public void initMonitorForClusterAndNodes(final String clusterId) {
        ClusterBO redisClusterBO = clusterProcessService.getRedisClusterById(clusterId);
        this.initMonitorForNodes(
                redisClusterBO.getInstanceBOs().stream().map(InstanceBO::getNode).collect(Collectors.toList()));
        this.initMonitorForCluster(redisClusterBO);
    }

    @Override
    public void initMonitorForInstances(final List<InstanceBO> instanceBOs) {
        if (monitorType == MonitorType.GRAFANA) {
            metricCollectionService.addInstances(instanceBOs);
        }
    }

    @Override
    public RedisMonitorInfo scrapeMonitorInfo(final Integer instanceId) {
        InstanceBO instanceBO = instanceProcessService.getInstanceBOById(instanceId);
        Map<String, String> monitorInfo = redisClientUtilityService.scrapeMonitorInfo(instanceBO);
        return convertToRedisMonitorInfo(monitorInfo, instanceBO);
    }

    private RedisMonitorInfo convertToRedisMonitorInfo(final Map<String, String> monitorInfoMap,
            final InstanceBO instanceBO) {
        RedisMonitorInfo redisMonitorInfo = RedisMonitorInfo.builder().scrapeTime(new Date())
                .keyspaceHits(Long.valueOf(monitorInfoMap.get(Constant.RedisInfo.KEYSPACE_HITS)))
                .keyspaceMisses(Long.valueOf(monitorInfoMap.get(Constant.RedisInfo.KEYSPACE_MISSES)))
                .usedMemory(Long.valueOf(monitorInfoMap.get(Constant.RedisInfo.USED_MEMORY)))
                .connectedClients(Long.valueOf(monitorInfoMap.get(Constant.RedisInfo.CONNECTED_CLIENTS)))
                .instantaneousOutputKbps(monitorInfoMap.containsKey(Constant.RedisInfo.INSTANTANEOUS_OUTPUT_KBPS)
                        ? Double.valueOf(monitorInfoMap.get(Constant.RedisInfo.INSTANTANEOUS_OUTPUT_KBPS))
                        : 0).build();

        redisMonitorInfo.setUsedMemoryPercent(
                (float) redisMonitorInfo.getUsedMemory()
                        / (instanceBO.getCacheSize() * CapacityUnit.GB.getBytes()) * 100);
        if (redisMonitorInfo.getKeyspaceHits() == 0L) {
            redisMonitorInfo.setKeyspaceHitPercent(0.0F);
        } else {
            redisMonitorInfo.setKeyspaceHitPercent(
                    (float) redisMonitorInfo.getKeyspaceHits()
                            / (redisMonitorInfo.getKeyspaceHits() + redisMonitorInfo.getKeyspaceMisses()) * 100);
        }

        return redisMonitorInfo;
    }
}
