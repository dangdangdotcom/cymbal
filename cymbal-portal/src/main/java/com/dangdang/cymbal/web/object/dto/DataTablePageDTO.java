package com.dangdang.cymbal.web.object.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO of data table with page.
 * Data table is a java script plugin.
 *
 * @auther GeZhen
 */
@Getter
@Setter
@Builder
public class DataTablePageDTO<T> {

    private Integer draw;

    private Integer recordsTotal;

    private Integer recordsFiltered;

    private String error;

    private List<T> data;
}
