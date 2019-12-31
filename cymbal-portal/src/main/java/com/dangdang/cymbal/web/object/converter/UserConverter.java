package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.dto.UserDTO;
import com.dangdang.cymbal.domain.po.User;
import org.springframework.stereotype.Component;

/**
 *
 * @auther GeZhen
 */
@Component
public class UserConverter extends BaseConverter<User, UserDTO> {

    @Override
    void poToDto(final User user, final UserDTO userDTO) {
    }

    @Override
    void dtoToPo(final UserDTO userDTO, final User user) {
    }
}
