package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.dto.ClusterPermissionDTO;
import com.dangdang.cymbal.domain.po.ClusterPermission;
import org.springframework.stereotype.Component;

/**
 *
 * @auther GeZhen
 */
@Component
public class ClusterPermissionConverter extends BaseConverter<ClusterPermission, ClusterPermissionDTO> {

    @Override
    void poToDto(final ClusterPermission clusterPermission, final ClusterPermissionDTO clusterPermissionDTO) {

    }

    @Override
    void dtoToPo(final ClusterPermissionDTO clusterPermissionDTO, final ClusterPermission clusterPermission) {

    }
}
