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
import com.skycloud.base.authentication.model.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资源表
 *
 * @author code generator
 * @date 2019-09-11 17:47:27
 */
@Mapper
@Repository
public interface ResourceMapper extends MyMapper<Resource> {
    /**
     * 根据用户信息获取resource
     *
     * @param userDto
     * @return
     */
    List<Resource> listResourceByUserId(UserDto userDto);

    /**
     * 根据角色code获取资源信息
     *
     * @param codes
     * @param types
     * @return
     */
    List<Resource> listResourceByRoleCodes(@Param("codes") String[] codes,@Param("types") Integer[] types);

    /**
     * 获取所有API URL
     *
     * @param resource
     * @param types
     * @return
     */
    List<Resource> listApiURL(Resource resource, @Param("types") Integer[] types);
}
