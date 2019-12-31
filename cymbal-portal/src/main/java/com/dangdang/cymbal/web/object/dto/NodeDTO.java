package com.dangdang.cymbal.web.object.dto;

import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.NodeStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO of {@link com.dangdang.cymbal.domain.po.Node}.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class NodeDTO {

    private Integer id;

    private String ip;

    private String host;

    private Environment env;

    private InternetDataCenter idc;

    private Integer totalMemory;

    private Integer freeMemory;

    private String password;

    private NodeStatus status;

    private String description;

    private int clusterCount;

    private int instanceCount;

    private int assignedMemory;
}
