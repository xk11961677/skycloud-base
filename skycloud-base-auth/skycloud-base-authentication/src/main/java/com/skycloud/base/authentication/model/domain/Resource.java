package com.skycloud.base.authentication.model.domain;

import com.sky.framework.web.mybatis.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 */
@Data
@NoArgsConstructor
public class Resource extends BaseEntity {
    private String code;
    private String name;
    private String type;
    private String url;
    private String method;
    private String description;
}
