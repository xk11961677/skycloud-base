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
package com.skycloud.base.authentication.mapper;

import com.sky.framework.mybatis.MyMapper;
import com.skycloud.base.authentication.model.domain.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author
 */
@Mapper
@Repository
public interface ResourceMapper extends MyMapper<Resource> {

    @Select("SELECT id,code,type,name,url,method,description,created_time,updated_time,created_by,updated_by" +
            " FROM dl_resources")
    Set<Resource> findAll();

    @Select("<script>" +
            "SELECT DISTINCT rs.code,rs.url,rs.name,rs.type,rs.method,rs.description" +
            " FROM dl_role r" +
            " INNER JOIN dl_role_resources_relation rrr ON r.id = rrr.role_id" +
            " INNER JOIN dl_resources rs ON rs.id = rrr.resource_id" +
            " WHERE r.code IN " +
            " <fosky collection='roleCodes' item='roleCode' index='index' open='(' close=')' separator=',' >" +
            "    #{roleCode}" +
            " </fosky>" +
            "</script>")
    Set<Resource> queryByRoleCodes(@Param("roleCodes") String[] roleCodes);
}