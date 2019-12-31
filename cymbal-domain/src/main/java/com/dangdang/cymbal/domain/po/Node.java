package com.dangdang.cymbal.domain.po;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Model for server machine.
 *
 * @author GeZhen
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Node {

    private Integer id;

    @EqualsAndHashCode.Include
    private String ip;

    private String host;

    private Environment env;

    private InternetDataCenter idc;

    private Integer totalMemory;

    private Integer freeMemory;

    private String password;

    private NodeStatus status;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
