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
package com.skycloud.base.authentication.api.service.impl;

import com.sky.framework.model.enums.SystemErrorCodeEnum;
import com.skycloud.base.authentication.api.service.AuthService;
import com.skycloud.base.authentication.api.service.AuthStrategy;
import com.sky.framework.model.dto.MessageRes;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author
 */
@Component
public class DefaultAuthStrategy implements AuthStrategy {

    protected static final String DEFAULT_AUTH_NAME = "default";

    @Resource
    private AuthService authService;

    /**
     * 验证
     *
     * @param authorization
     * @param url
     * @param method
     * @return
     */
    @Override
    public MessageRes auth(String authorization, String url, String method) {
        MessageRes result = MessageRes.success();
        String userId = authService.checkJwtRedis(authorization);
        if (StringUtils.isEmpty(userId)) {
            result = MessageRes.fail(SystemErrorCodeEnum.AUZ100016.getCode(), SystemErrorCodeEnum.AUZ100016.getMsg());
        }
        result.setData(userId);
        return result;
    }

    @Override
    public String name() {
        return DEFAULT_AUTH_NAME;
    }
}
