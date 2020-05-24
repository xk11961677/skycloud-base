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
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.enums.SystemErrorCodeEnum;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.skycloud.base.authentication.api.model.dto.CustomLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author
 */
@Slf4j
public class RequestUtils {

    /**
     * 获取json参数
     *
     * @param request
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getJsonParameters(HttpServletRequest request, Class<? extends CustomLoginDTO> clazz) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();
            T t = (T) (StringUtils.isEmpty(body) ? clazz.newInstance() : JSON.parseObject(body, clazz));
            return t;
        } catch (Exception e) {
            LogUtils.error(log, ":{}", e.getMessage());
            throw new AuzBussinessException(SystemErrorCodeEnum.GL990001.getCode(), SystemErrorCodeEnum.GL990001.getMsg());
        }
    }
}
