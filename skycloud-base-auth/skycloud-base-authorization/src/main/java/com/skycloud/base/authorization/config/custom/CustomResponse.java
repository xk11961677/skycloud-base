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
package com.skycloud.base.authorization.config.custom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.sky.framework.model.exception.BusinessException;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @author
 */
@Slf4j
public class CustomResponse {

    /**
     * 响应自定义错误码
     *
     * @param response
     * @param auze
     */
    public static void response(HttpServletResponse response, AuzBussinessException auze) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            String resp = null;
            JSONObject data = auze.getData();
            if (data != null) {
                data.put("code", auze.getCode());
                data.put("msg", auze.getMessage());
                resp = data.toJSONString();
            } else {
                resp = JSON.toJSONString(MessageRes.fail(auze.getCode(), auze.getMessage()));
            }
            response.getWriter().write(resp);
        } catch (Exception e) {
            LogUtils.error(log, "response msg exception:{}", e.getMessage());
        }
    }


    /**
     * 响应自定义错误码
     *
     * @param response
     * @param auze
     */
    public static void response(HttpServletResponse response, BusinessException auze) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            String resp = JSON.toJSONString(MessageRes.fail(auze.getCode(), auze.getMessage()));
            response.getWriter().write(resp);
        } catch (Exception e) {
            LogUtils.error(log, "response msg exception:{}", e.getMessage());
        }
    }


    public static void response(HttpServletResponse response, String content) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(content);
        } catch (Exception e) {
            LogUtils.error(log, "response msg exception:{}", e.getMessage());
        }
    }
}
