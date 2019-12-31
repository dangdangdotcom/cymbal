package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Instance;
import com.dangdang.cymbal.web.object.dto.InstanceDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter of {@link Instance}.
 *
 * @auther GeZhen
 */
@Component
public class InstanceConverter extends BaseConverter<Instance, InstanceDTO> {

    @Resource
    private ClusterEntityService clusterEntityService;

    /**
     * Converter Instance BOs to DTOs.
     *
     * @param instanceBOS redis server instance BOs
     * @return redis server instance DTOs
     */
    public List<InstanceDTO> bosToDtos(final List<InstanceBO> instanceBOS) {
        List<InstanceDTO> dtos = new ArrayList(instanceBOS.size());
        instanceBOS.forEach(each -> {
            dtos.add(boToDto(each));
        });
        return dtos;
    }

    public InstanceDTO boToDto(final InstanceBO instanceBO) {
        InstanceDTO dto = new InstanceDTO();
        BeanUtils.copyProperties(instanceBO.getSelf(), dto);
        Cluster cluster = clusterEntityService.getByClusterId(dto.getClusterId());

        dto.setUserName(cluster.getUserName());
        dto.setUserCnName(cluster.getUserCnName());
        dto.setCacheSize(cluster.getCacheSize());
        dto.setClusterDescription(cluster.getDescription());

        dto.setIp(instanceBO.getNode().getIp());
        dto.setNodeId(instanceBO.getNode().getId());
        dto.setNodeDescription(instanceBO.getNode().getDescription());
        return dto;
    }

    @Override
    void poToDto(Instance instance, InstanceDTO instanceDTO) {
    }

    @Override
    void dtoToPo(InstanceDTO instanceDTO, Instance instance) {
    }
}
