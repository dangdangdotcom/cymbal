package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.dto.ClusterScaleDTO;
import com.dangdang.cymbal.domain.po.ClusterScale;
import org.springframework.stereotype.Component;

/**
 * Converter for redis cluster scale PO and DTO.
 *
 * @auther GeZhen
 */
@Component
public class ClusterScaleConverter extends BaseConverter<ClusterScale, ClusterScaleDTO> {

    @Override
    void poToDto(final ClusterScale clusterScale, final ClusterScaleDTO clusterScaleDTO) {

    }

    @Override
    void dtoToPo(final ClusterScaleDTO clusterScaleDTO, final ClusterScale clusterScale) {

    }
}
