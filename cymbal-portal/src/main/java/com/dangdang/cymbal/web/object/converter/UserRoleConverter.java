package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.dto.UserRoleDTO;
import com.dangdang.cymbal.domain.po.UserRole;
import org.springframework.stereotype.Component;

/**
 *
 * @auther GeZhen
 */
@Component
public class UserRoleConverter extends BaseConverter<UserRole, UserRoleDTO> {

    @Override
    void poToDto(UserRole userRole, UserRoleDTO userRoleDTO) {

    }

    @Override
    void dtoToPo(UserRoleDTO userRoleDTO, UserRole userRole) {
        userRole.setRoleId(1);
    }
}
