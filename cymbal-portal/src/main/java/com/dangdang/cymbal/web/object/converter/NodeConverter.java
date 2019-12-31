package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.web.object.dto.NodeDTO;
import com.dangdang.cymbal.domain.po.Node;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Converter of {@link Node} and {@link NodeDTO}.
 *
 * @auther GeZhen
 */
@Component
public class NodeConverter extends BaseConverter<Node, NodeDTO> {

    @Resource
    private InstanceEntityService instanceEntityService;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Override
    void poToDto(Node node, NodeDTO nodeDTO) {
    }

    @Override
    void dtoToPo(NodeDTO nodeDTO, Node node) {
    }
}
