package com.dangdang.cymbal.service.util.service.impl;

import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.service.util.enums.AnsiblePlayBookName;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implement of AnsibleService.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class AnsibleServiceImpl implements AnsibleService {

    @Resource
    private ShellService shellService;

    @Override
    public List<Node> runPlayBookOnNodes(final AnsiblePlayBookName ansiblePlayBookName, final List<Node> nodes) {
        Preconditions.checkArgument(!nodes.isEmpty(), "Nodes can not be null or empty.");

        StringBuilder ipAndPasswords = new StringBuilder();
        for (Node each : nodes) {
            ipAndPasswords.append(each.getIp()).append(" '");
            ipAndPasswords.append(each.getPassword()).append("' ");
        }

        List<String> ansibleResult = shellService
                .execAnsibleShellScript(ansiblePlayBookName, ipAndPasswords.toString());
        return getRunSucceedNodes(nodes, ansibleResult);
    }

    private List<Node> getRunSucceedNodes(final List<Node> originNodes, final List<String> ansibleResult) {
         /*
         * ansible执行结果示例：
        TASK [make redis] **************************************************************
        changed: [192.168.91.182] => (item=redis-2.8.24)
        changed: [192.168.91.200] => (item=redis-2.8.24)
        changed: [192.168.91.182] => (item=redis-3.2.11)
        changed: [192.168.91.200] => (item=redis-3.2.11)

        PLAY RECAP *********************************************************************
        192.168.91.182             : ok=7    changed=2    unreachable=0    failed=0
        192.168.91.200             : ok=7    changed=2    unreachable=0    failed=0
        */

        List<Node> succeedNodes = new ArrayList<>();

        StringBuilder ansibleResultStringBuilder = new StringBuilder();
        for (String line : ansibleResult) {
            ansibleResultStringBuilder.append(line).append("\n");
        }
        log.info("Result of ansible: {}", ansibleResultStringBuilder.toString());

        Map<String, Node> ipToNodeMap = getIpToNodeMap(originNodes);
        String IP_PATTERN = "(?<ip>(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})";
        String RESULT_PATTERN = "\\s+:.ok=(?<ok>\\d*)\\s+changed=(?<changed>\\d*)\\s+unreachable=(?<unreachable>\\d*)\\s+failed=(?<failed>\\d*)";
        Pattern p = Pattern.compile(IP_PATTERN + RESULT_PATTERN);
        Matcher m = p.matcher(ansibleResultStringBuilder.toString());
        while (m.find()) {
            String ip = m.group("ip");
            int unreachable = Integer.valueOf(m.group("unreachable"));
            int failed = Integer.valueOf(m.group("failed"));

            if (unreachable == 0 && failed == 0) {
                succeedNodes.add(ipToNodeMap.get(ip));
            }
        }

        return succeedNodes;
    }

    private Map<String, Node> getIpToNodeMap(final List<Node> nodes) {
        Map<String, Node> ipToNodeMap = new HashMap<>();
        for (Node each : nodes) {
            ipToNodeMap.put(each.getIp(), each);
        }
        return ipToNodeMap;
    }
}
