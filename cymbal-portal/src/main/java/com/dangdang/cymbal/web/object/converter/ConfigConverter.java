package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.web.object.dto.ConfigDTO;
import org.springframework.stereotype.Component;

/**
 * Converter for redis config PO and DTO.
 *
 * @auther GeZhen
 */
@Component
public class ConfigConverter extends BaseConverter<Config, ConfigDTO> {

    @Override
    void poToDto(Config config, ConfigDTO configDTO) {

    }

    @Override
    void dtoToPo(ConfigDTO configDTO, Config config) {

    }
}
