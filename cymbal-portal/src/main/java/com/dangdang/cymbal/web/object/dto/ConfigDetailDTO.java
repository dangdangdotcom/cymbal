package com.dangdang.cymbal.web.object.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Redis config detail DTO.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class ConfigDetailDTO {

    private Integer id;

    private Integer configId;

    private String itemName;

    private String itemValue;

    private String itemComment;
}
