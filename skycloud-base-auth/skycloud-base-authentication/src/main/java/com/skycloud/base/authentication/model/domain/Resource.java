/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.skycloud.base.authentication.model.domain;

import com.sky.framework.web.support.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 资源表
 *
 * @author code generator
 * @date 2019-09-11 17:47:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_resource")
public class Resource extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源code
     */
    private String code;
    /**
     * 资源类型(10 左侧菜单 20 top菜单 30 接口URL 40 按钮)
     */
    private String type;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源url
     */
    private String url;
    /**
     * 资源方法
     */
    private String method;
    /**
     * 简介
     */
    private String description;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 父ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 图标
     */
    private String icon;

}
