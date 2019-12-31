package com.dangdang.cymbal.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Work with annotation {@link TableField} when {@link com.baomidou.mybatisplus.annotation.FieldFill fill} attribute is set.
 *
 * @auther GeZhen
 */
@Component
public class DateMetaObjectHandler implements MetaObjectHandler {

    private static final String FIELD_NAME_CREATION_DATE = "creationDate";
    private static final String FIELD_NAME_LAST_CHANGED_DATE = "lastChangedDate";

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        this.setInsertFieldValByName(FIELD_NAME_CREATION_DATE, now, metaObject);
        this.setInsertFieldValByName(FIELD_NAME_LAST_CHANGED_DATE, now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
