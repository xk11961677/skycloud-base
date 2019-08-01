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
package com.skycloud.base.authorization.config.custom.filter;

import com.alibaba.fastjson.JSON;
import com.skycloud.base.authorization.common.Constants;
import com.skycloud.base.authorization.config.custom.CustomResponse;
import com.skycloud.base.authorization.config.custom.token.UserPasswordAuthenticationToken;
import com.skycloud.base.authorization.exception.AuthErrorType;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.skycloud.base.authorization.model.dto.UserPasswordLoginDto;
import com.sky.framework.common.IpUtils;
import com.sky.framework.common.LogUtils;
import com.sky.framework.common.validation.ValidateUtils;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 自定义用户名密码登录
 *
 * @author
 */
@Slf4j
public class UserPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 匹配路径
     */
    private static final String MATCHER_URL = "/oauth/login";

    /**
     * 是否只处理post请求
     */
    private boolean postOnly = true;


    public UserPasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher(MATCHER_URL, Constants.METHOD));
    }

    @SuppressWarnings("all")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            if (this.postOnly && !request.getMethod().equals(Constants.METHOD)) {
                throw new AuzBussinessException(AuthErrorType.UNSUPPORTED_RESPONSE_TYPE.getCode(), AuthErrorType.UNSUPPORTED_RESPONSE_TYPE.getMsg());
            }

            String channel = request.getHeader(Constants.CHANNEL);
            String clientIp = IpUtils.getClientIp(request);

            UserPasswordLoginDto userPasswordLoginDto = getParamFromApplicationJson(request);
            userPasswordLoginDto.setChannel(channel);
            userPasswordLoginDto.setLoginIp(clientIp);

            ValidateUtils.validThrowFailFast(userPasswordLoginDto);

            UserPasswordAuthenticationToken token = new UserPasswordAuthenticationToken(userPasswordLoginDto);
            this.setDetails(request, token);
            return this.getAuthenticationManager().authenticate(token);
        } catch (AuzBussinessException e) {
            CustomResponse.response(response, e);
            LogUtils.error(log, "username password login AuzException:{}", e);
        } catch (BusinessException e) {
            CustomResponse.response(response, e);
            LogUtils.error(log, "mobile login AuzException:{}", e);
        } catch (Exception e) {
            MessageRes fail = MessageRes.fail(AuthErrorType.AUZ100026.getCode(), e.getMessage() == null ? AuthErrorType.AUZ100026.getMsg() : e.getMessage());
            CustomResponse.response(response, JSON.toJSONString(fail));
            LogUtils.error(log, "username password login exception:{}", e);
        }
        return null;
    }


    private void setDetails(HttpServletRequest request, UserPasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }


    private UserPasswordLoginDto getParamFromApplicationJson(HttpServletRequest request) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();
            return JSON.parseObject(body, UserPasswordLoginDto.class);
        } catch (Exception e) {
            LogUtils.error(log, "get param exception:{}", e);
        }
        return new UserPasswordLoginDto();
    }


}
