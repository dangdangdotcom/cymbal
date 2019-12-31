package com.dangdang.cymbal.service.monitor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.service.monitor.exception.MonitorException;
import com.dangdang.cymbal.service.monitor.service.MetricCollectionService;
import com.dangdang.cymbal.service.util.enums.AnsiblePlayBookName;
import com.dangdang.cymbal.service.util.exception.ShellExecutionException;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.dangdang.cymbal.service.util.service.SshClientService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implement of MetricCollectionService by prometheus.
 *
 * @author GeZhen
 */
@Slf4j
@Service
public class PrometheusMetricCollectionServiceImpl implements MetricCollectionService {

    /**
     * Prefix for prometheus config file of redis exporter.
     */
    private final static String PROMETHEUS_JOB_CONFIG_PREFIX_REDIS_EXPORTER = "redis_exporter";

    @Value("${monitor.prometheus.host}")
    private String prometheusHost;

    @Value("${monitor.prometheus.confd.path}")
    private String prometheusConfdPath;

    @Value("${monitor.exporter.redis.path}")
    private String redisExporterPath;

    @Value("${monitor.exporter.redis.port}")
    private int redisExporterPort;

    @Value("${monitor.exporter.redis.confd.path}")
    private String redisExporterConfdPath;

    @Value("${monitor.exporter.redis.script.name}")
    private String redisExporterScriptName;

    @Resource
    private SshClientService sshClientService;

    @Resource
    private AnsibleService ansibleService;

    @Override
    public void addNode(final Node node) {
        addNodes(Arrays.asList(node));
    }

    @Override
    public void addNodes(final List<Node> nodes) {
        Preconditions.checkArgument(!nodes.isEmpty(), "Nodes can not be null or empty.");
        removeDuplicate(nodes);
        installExporterForNodes(nodes);
        registerExporterToPrometheus(nodes);
    }

    private void removeDuplicate(final List<Node> nodes) {
        if (nodes.size() == 1) {
            return;
        }
        Set<Node> nodesWithoutDuplicate = new HashSet<>(nodes);
        nodes.clear();
        nodes.addAll(nodesWithoutDuplicate);
    }

    private void installExporterForNodes(final List<Node> nodes) {
        ansibleService.runPlayBookOnNodes(AnsiblePlayBookName.MONITOR_INIT, nodes);
    }

    private void registerExporterToPrometheus(final List<Node> nodes) {
        JSONArray root = new JSONArray();
        JSONArray targets = new JSONArray();
        JSONObject job = new JSONObject();
        job.put("targets", targets);
        root.add(job);

        for (Node node : nodes) {
            targets.clear();
            targets.add(String.format("%s:%d", node.getIp(), redisExporterPort));
            String fileName = String.format("%s_%s.json", PROMETHEUS_JOB_CONFIG_PREFIX_REDIS_EXPORTER, node.getIp());
            try {
                sshClientService.createNewFile(prometheusHost, fileName, root.toJSONString(), prometheusConfdPath);
            } catch (ShellExecutionException e) {
                throw new MonitorException(e);
            }
        }
    }

    @Override
    public void addInstance(final String host, final int port, final String clusterId, final String password)
            throws MonitorException {
        // Name of config file is 'host:port'.
        String fileName = String.format("%s:%s", host, port);
        // Content of config file is 'cluster,password'.
        String fileContent = String.format("%s,%s", clusterId, Strings.nullToEmpty(password));

        try {
            // Create config file for target redis instance, and restart exporter service.
            sshClientService.createNewFile(host, fileName, fileContent, redisExporterConfdPath);
            try {
                sshClientService.executeCommandByRoot(host, "systemctl restart redis_exporter.service");
            } catch (ShellExecutionException e) {
                sshClientService.executeShellScript(host, redisExporterPath, redisExporterScriptName);
            }
        } catch (ShellExecutionException e) {
            throw new MonitorException(e);
        }
    }

    @Override
    public void addInstances(final List<InstanceBO> instanceBOs) {
        instanceBOs.forEach(each -> {
            this.addInstance(each.getNode().getIp(), each.getSelf().getPort(), each.getSelf().getClusterId(),
                    each.getPassword());
        });
    }
}
