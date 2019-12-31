package com.dangdang.cymbal.web.object.converter;

import com.dangdang.cymbal.web.object.dto.DataTablePageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 *
 * @auther GeZhen
 */
@Component
public class PageConverter<T> {

    /**
     * Convert page to data table page DTO.
     *
     * @param page page
     * @param draw for data table
     * @return data table page DTO
     */
    public DataTablePageDTO<T> toDTO(final Page page, final Integer draw) {
        return DataTablePageDTO.builder().data(page.getContent()).recordsFiltered((int) page.getTotalElements())
                .recordsTotal((int) page.getTotalElements()).draw(draw).build();
    }
}
