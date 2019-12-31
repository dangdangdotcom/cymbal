package com.dangdang.cymbal.web.object.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Redis cluster permission DTO.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class ClusterPermissionDTO {

    private Integer id;

    private String clusterId;

    private String userName;

    private String userCnName;
}
