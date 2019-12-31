package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Redis cluster privilege model.
 *
 * @author GeZhen
 */
@Getter
@Setter
@ToString
public class ClusterPermission {

    private Integer id;

    private String clusterId;

    private String userName;

    private String userCnName;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
