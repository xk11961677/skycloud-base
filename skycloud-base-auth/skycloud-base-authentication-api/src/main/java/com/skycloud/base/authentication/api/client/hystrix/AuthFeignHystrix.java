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
package com.skycloud.base.authentication.api.client.hystrix;

import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.skycloud.base.authentication.api.model.dto.UserLoginDto;
import com.skycloud.base.authentication.api.model.vo.UserLoginVo;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
public class AuthFeignHystrix implements AuthFeignApi {
    /**
     * 降级统一返回超时
     *
     * @param authentication
     * @param url
     * @param method
     */
    @Override
    public MessageRes auth(String authentication, String url, String method) {
        return MessageRes.fail(FailureCodeEnum.GL990002.getCode(), FailureCodeEnum.GL990002.getMsg());
    }

    @Override
    public MessageRes<UserLoginVo> login(UserLoginDto userLoginDto) {
        return MessageRes.fail(FailureCodeEnum.GL990002.getCode(), FailureCodeEnum.GL990002.getMsg());
    }
}