package com.dangdang.cymbal.service.monitor.service.impl;

import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterPermission;
import com.dangdang.cymbal.service.auth.service.entity.ClusterPermissionEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.monitor.service.AlertManagerService;
import com.dangdang.cymbal.service.util.MailUtil;
import com.dangdang.cymbal.service.util.service.MailService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handle alert event from prometheus.
 *
 * @Author: GeZhen
 */
@Slf4j
@Service
public class PrometheusAlertManagerServiceImpl implements AlertManagerService {

    private final static String KEY_COMMON_LABELS = "commonLabels";

    private final static String KEY_COMMON_ANNOTATIONS = "commonAnnotations";

    private final static String KEY_CLUSTER_ID = "alias";

    private final static String KEY_ALERTS = "alerts";

    private final static String KEY_SUMMARY = "summary";

    private final static String KEY_DESCRIPTION = "description";

    private final static String KEY_ADDR = "addr";

    private final static String KEY_LABELS = "labels";

    private final static String KEY_ANNOTATIONS = "annotations";

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private ClusterPermissionEntityService clusterPermissionEntityService;

    @Resource
    private MailService mailService;

    @Override
    public void handleAlert(Object alertInfo) {
        // prometheus的alert manager的报警结构体是一个map
        Map<String, Object> alertInfoMap = (Map<String, Object>) alertInfo;
        Map<String, String> commonLabels = (Map<String, String>) alertInfoMap.get(KEY_COMMON_LABELS);

        String clusterId = commonLabels.get(KEY_CLUSTER_ID);
        Cluster cluster = clusterEntityService.getByClusterId(clusterId);
        Preconditions.checkNotNull(cluster, String.format("Can not find cluster with id %s.", clusterId));

        Map<String, String> commonAnnotations = (Map<String, String>) alertInfoMap.get(KEY_COMMON_ANNOTATIONS);
        String alertTitle = commonAnnotations.get(KEY_SUMMARY);

        // 邮件内容，首先拼接集群描述
        StringBuilder mailContent = new StringBuilder();
        mailContent.append(MailUtil.getClusterInfoForHtmlMail(cluster));

        // 拼接报警信息描述
        List<Map<String, Object>> alerts = (List<Map<String, Object>>) alertInfoMap.get(KEY_ALERTS);
        appendAlertInstancesToMailContent(alerts, mailContent);

        // 收件人
        List<ClusterPermission> permissions = clusterPermissionEntityService.queryByClusterId(clusterId);
        List<String> receivers = permissions.stream().map(each -> String.format("%s@dangdang.com", each.getUserName()))
                .collect(Collectors.toList());
        receivers.add(0, String.format("%s@dangdang.com", cluster.getUserName()));

        String mailTitle = String.format("[OPS平台报警] [Redis异常] [%s] [%s]", alertTitle, cluster.getDescription());

        mailService.sendHtmlMail(mailTitle, mailContent.toString(), receivers.toArray(new String[receivers.size()]));
        // 测试状态下的收件人
        // mailService.sendMail(mailTitle, mailContent.toString(), new String[]{"gezhen@dangdang.com", "zhouliang@dangdang.com"}, null, true);
    }

    private void appendAlertInstancesToMailContent(List<Map<String, Object>> alerts, StringBuilder mailContent) {
        mailContent.append("<p>").append("异常节点列表: ").append("</p>");

        for (Map<String, Object> each : alerts) {
            String addr = ((Map<String, String>) each.get(KEY_LABELS)).get(KEY_ADDR);
            String description = ((Map<String, String>) each.get(KEY_ANNOTATIONS)).get(KEY_DESCRIPTION);

            mailContent.append("<p>").append(" - ").append(addr).append(": ").append(description).append("</p>");
        }
    }
}
