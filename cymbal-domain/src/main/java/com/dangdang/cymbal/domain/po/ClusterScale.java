package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Redis cluster scale model.
 *
 * @author GeZhen
 */
@Getter
@Setter
@ToString
public class ClusterScale implements Serializable {

    private Integer id;

    private String clusterId;

    private ScaleType type;

    private Integer scaleNum;

    private ScaleStatus status = ScaleStatus.DOING;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private ScaleResult result;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String resultDesc;

    private String operator;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
