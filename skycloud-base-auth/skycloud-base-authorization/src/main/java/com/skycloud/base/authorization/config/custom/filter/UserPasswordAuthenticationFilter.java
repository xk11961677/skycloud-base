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
import com.sky.framework.common.IpUtils;
import com.sky.framework.common.LogUtils;
import com.sky.framework.common.validation.ValidateUtils;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.exception.BusinessException;
import com.skycloud.base.authorization.common.Constants;
import com.skycloud.base.authorization.config.custom.CustomClientDetailService;
import com.skycloud.base.authorization.config.custom.RequestUtils;
import com.skycloud.base.authorization.config.custom.ResponseUtils;
import com.skycloud.base.authorization.config.custom.token.UserPasswordAuthenticationToken;
import com.skycloud.base.authorization.exception.AuthErrorType;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.skycloud.base.authorization.model.bo.ClientDetailsBo;
import com.skycloud.base.authorization.model.dto.UserPasswordLoginDto;
import com.skycloud.base.common.constant.BaseConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义用户名密码登录
 *
 * @author
 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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

    @Setter
    private CustomClientDetailService customClientDetailService;


    public UserPasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher(MATCHER_URL, Constants.POST_METHOD));
    }

    @SuppressWarnings("all")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            if (this.postOnly && !request.getMethod().equals(Constants.POST_METHOD)) {
                throw new AuzBussinessException(AuthErrorType.UNSUPPORTED_RESPONSE_TYPE.getCode(), AuthErrorType.UNSUPPORTED_RESPONSE_TYPE.getMsg());
            }

            String channel = request.getHeader(BaseConstants.CHANNEL);
            String clientIp = IpUtils.getClientIp(request);
            UserPasswordLoginDto userPasswordLoginDto = RequestUtils.getJsonParameters(request, UserPasswordLoginDto.class);
            userPasswordLoginDto.setChannel(channel);
            userPasswordLoginDto.setLoginIp(clientIp);
            ValidateUtils.validThrowFailFast(userPasswordLoginDto);

            ClientDetailsBo clientDetailsBo = customClientDetailService.verifyClient(request, "password");
            userPasswordLoginDto.setClientDetailsBo(clientDetailsBo);
            UserPasswordAuthenticationToken authRequest = new UserPasswordAuthenticationToken(userPasswordLoginDto);
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (AuzBussinessException e) {
            ResponseUtils.response(response, e);
            LogUtils.error(log, "username password login AuzException:{}", e);
        } catch (BusinessException e) {
            ResponseUtils.response(response, e);
            LogUtils.error(log, "username password login AuzException:{}", e);
        } catch (Exception e) {
            MessageRes fail = MessageRes.fail(AuthErrorType.AUZ100026.getCode(), e.getMessage() == null ? AuthErrorType.AUZ100026.getMsg() : e.getMessage());
            ResponseUtils.response(response, JSON.toJSONString(fail));
            LogUtils.error(log, "username password login exception:{}", e);
        }
        return null;
    }


    private void setDetails(HttpServletRequest request, UserPasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
