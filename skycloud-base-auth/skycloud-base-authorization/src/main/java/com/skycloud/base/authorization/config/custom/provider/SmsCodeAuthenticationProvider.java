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
package com.skycloud.base.authorization.config.custom.provider;

import com.alibaba.fastjson.JSONObject;
import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.authorization.client.AdUserFeignApi;
import com.skycloud.base.authorization.client.dto.CustomLoginDto;
import com.skycloud.base.authorization.config.custom.CustomUserDetail;
import com.skycloud.base.authorization.config.custom.token.SmsCodeAuthenticationToken;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.skycloud.base.authorization.model.dto.MobileLoginDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 短信验证码登录provider
 *
 * @author
 */
@Component
@Slf4j
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private AdUserFeignApi adUserFeignApi;

    @Autowired
    private MapperFacade mapperFacade;

    @SuppressWarnings("all")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        MobileLoginDto mobileLoginDTO = (MobileLoginDto) authenticationToken.getPrincipal();
        //建行登录流程todo

        //通用登录流程
        CustomLoginDto dto = mapperFacade.map(mobileLoginDTO, CustomLoginDto.class);
        dto.setLoginName(mobileLoginDTO.getMobile());
        MessageRes<CustomLoginDto> login = adUserFeignApi.login(dto);
        if(!login.isSuccess()) {
            throw new AuzBussinessException(login.getCode(),login.getMsg());
        }
        CustomLoginDto customLoginDto = login.getData();
        JSONObject data = new JSONObject();
        data.put("userInfo", customLoginDto);
        CustomUserDetail customUserDetail = new CustomUserDetail(Long.valueOf(customLoginDto.getLoginName()), customLoginDto.getLoginName(), customLoginDto.getLoginName());
        SmsCodeAuthenticationToken result = new SmsCodeAuthenticationToken(customLoginDto.getLoginName(), data);
        result.setDetails(customUserDetail);
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
