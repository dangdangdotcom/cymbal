package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.service.monitor.bean.RedisMonitorInfo;
import com.dangdang.cymbal.service.monitor.exception.MonitorException;
import com.dangdang.cymbal.service.monitor.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
public class MonitorController extends BaseController {

    @Resource
    private MonitorService monitorService;

    @GetMapping(value = "/cluster/monitors/page")
    public String clusterMonitorPage() {
        return "/monitor/cluster_monitor_total_grafana";
    }

    @PostMapping(value = "/clusters/{clusterId}/monitors")
    @ResponseBody
    public ResponseEntity<String> initMonitorForCluster(final @PathVariable String clusterId) {
        try {
            monitorService.initMonitorForClusterAndNodes(clusterId);
            return ResponseEntity.ok().build();
        } catch (MonitorException e) {
            log.error(String.format("Apply monitor fail for cluster: '%s'.", clusterId), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = "/instances/{instanceId}/monitors")
    @ResponseBody
    public RedisMonitorInfo getRedisMonitorInfo(final @PathVariable Integer instanceId) {
        return monitorService.scrapeMonitorInfo(instanceId);
    }
}
