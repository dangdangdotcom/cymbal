package com.dangdang.cymbal.web.object.dto;

import com.dangdang.cymbal.domain.po.ScaleResult;
import com.dangdang.cymbal.domain.po.ScaleStatus;
import com.dangdang.cymbal.domain.po.ScaleType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Cluster scale DTO.
 *
 * @auther GeZhen
 */
@Getter
@Setter
public class ClusterScaleDTO {

    private Integer id;

    private String clusterId;

    private ScaleType type;

    private Integer scaleNum;

    private ScaleStatus status = ScaleStatus.DOING;

    private ScaleResult result;

    private String resultDesc;

    private String operator;

    private Date creationDate;

    private Date lastChangedDate;
}
