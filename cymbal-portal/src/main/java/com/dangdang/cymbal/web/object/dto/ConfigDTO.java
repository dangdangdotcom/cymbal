package com.dangdang.cymbal.web.object.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Redis config DTO.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class ConfigDTO {

    private Integer id;

    private String configName;

    private String userCnName;

    private String clusterId;

    private String redisVersion;

    private Date creationDate;
}
