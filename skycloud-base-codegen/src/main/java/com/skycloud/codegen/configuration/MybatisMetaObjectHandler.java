package com.skycloud.codegen.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 通用的字段填充，如createBy createDate这些字段的自动填充
 * @author
 */
@Component
@Slf4j
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        this.setInsertFieldValByName("createdBy", "system", metaObject);
        this.setInsertFieldValByName("updatedBy", "system", metaObject);
        Date date = new Date();
        this.setInsertFieldValByName("createdTime", date, metaObject);
        this.setInsertFieldValByName("updatedTime", date, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //todo updateBY
        log.debug("start update fill ....");
        this.setInsertFieldValByName("updatedBy", "system", metaObject);
        Date date = new Date();
        this.setUpdateFieldValByName("updatedTime", date, metaObject);
    }
}