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
package com.skycloud.codegen.util;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

/**
 * 分页工具类
 *
 * @author
 */
public class PageUtils {
    /**
     * 用于保存分页查询用到的关键字
     */
    public static final List<String> PAGE_QUERY_KEY_LIST = ImmutableList.of("current", "size", "sortBy", "orderby", "order", "sort", "ifCount", "ascs", "descs");


    /**
     * 过滤pageHelper的参数、空值等
     * 返回查询条件
     *
     * @return
     */
    public static Map<String, Object> filterColumnQueryParams(Map<String, Object> map) {
        return MapUtil.filter(map, (Filter<Map.Entry<String, Object>>) e -> {
            if (StrUtil.isBlank(StrUtil.toString(e.getValue()))) {
                return false;
            }
            if (PAGE_QUERY_KEY_LIST.contains(e.getKey())) {
                return false;
            }
            return true;
        });
    }

    /**
     * 返回pageHelper用到的参数
     *
     * @return
     */
    public static Map<String, Object> filterPageParams(Map<String, Object> map) {
        return MapUtil.filter(map, (Filter<Map.Entry<String, Object>>) e -> {
            if (StrUtil.isBlank(StrUtil.toString(e.getValue()))) {
                return false;
            }
            if (PAGE_QUERY_KEY_LIST.contains(e.getKey())) {
                return true;
            }
            return false;
        });
    }
}
