package com.dangdang.cymbal.service.util.service;

import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.service.util.enums.AnsiblePlayBookName;

import java.util.List;

/**
 * Service for run ansible play book script to nodes.
 *
 * @auther GeZhen
 */
public interface AnsibleService {

    /**
     * Run playbook on target nodes.
     *
     * @param ansiblePlayBookName name of playbook to run
     * @param nodes target nodes
     * @return nodes which run succeed
     */
    List<Node> runPlayBookOnNodes(AnsiblePlayBookName ansiblePlayBookName, List<Node> nodes);
}
