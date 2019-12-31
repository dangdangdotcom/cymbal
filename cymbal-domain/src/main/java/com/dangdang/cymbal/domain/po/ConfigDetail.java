package com.dangdang.cymbal.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class ConfigDetail {

    private Integer id;

    private Integer configId;

    private String itemName;

    private String itemValue;

    private ConfigDetailStatus status;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT, update = "now()")
    private Date lastChangedDate;
}
