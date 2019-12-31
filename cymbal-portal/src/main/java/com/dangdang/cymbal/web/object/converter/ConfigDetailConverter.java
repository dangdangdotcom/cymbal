package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.domain.bo.ConfigDetailBO;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.web.object.dto.ConfigDetailDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Redis config detail converter.
 *
 * @auther GeZhen
 */
@Component
public class ConfigDetailConverter extends BaseConverter<ConfigDetail, ConfigDetailDTO> {

    @Override
    void poToDto(final ConfigDetail configDetail, final ConfigDetailDTO configDetailDTO) {

    }

    @Override
    void dtoToPo(final ConfigDetailDTO configDetailDTO, final ConfigDetail configDetail) {

    }

    public ConfigDetailDTO boToDto(final ConfigDetailBO bo) {
        ConfigDetailDTO dto = new ConfigDetailDTO();
        if (Objects.nonNull(bo.getConfigDetail())) {
            dto.setId(bo.getConfigDetail().getId());
            dto.setConfigId(bo.getConfigDetail().getConfigId());
            dto.setItemName(bo.getConfigDetail().getItemName());
            dto.setItemValue(bo.getConfigDetail().getItemValue());
        } else {
            dto.setItemName(bo.getConfigDict().getItemName());
            dto.setItemValue(bo.getConfigDict().getDefaultItemValue());
        }
        dto.setItemComment(bo.getConfigDict().getItemComment());
        return dto;
    }

    public List<ConfigDetailDTO> bosToDtos(final List<ConfigDetailBO> bos) {
        List<ConfigDetailDTO> dtos = new ArrayList<>();
        bos.forEach(eachBO -> {
            dtos.add(boToDto(eachBO));
        });
        return dtos;
    }
}
