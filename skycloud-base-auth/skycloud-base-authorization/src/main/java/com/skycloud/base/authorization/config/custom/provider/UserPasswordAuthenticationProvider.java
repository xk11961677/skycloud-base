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
import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.skycloud.base.authentication.api.model.dto.UserLoginDto;
import com.skycloud.base.authentication.api.model.vo.RoleVo;
import com.skycloud.base.authentication.api.model.vo.UserLoginVo;
import com.skycloud.base.authorization.client.AdUserFeignApi;
import com.skycloud.base.authorization.client.dto.CustomLoginDto;
import com.skycloud.base.authorization.config.custom.CustomUserDetail;
import com.skycloud.base.authorization.config.custom.token.UserPasswordAuthenticationToken;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.skycloud.base.authorization.model.dto.UserPasswordLoginDto;
import com.skycloud.base.common.enums.ChannelTypeEnums;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 短信验证码登录provider
 *
 * @author
 */
@Component
@Slf4j
public class UserPasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AdUserFeignApi adUserFeignApi;

    @Autowired
    private AuthFeignApi authFeignApi;

    @Autowired
    private MapperFacade mapperFacade;

    @SuppressWarnings("all")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserPasswordAuthenticationToken authenticationToken = (UserPasswordAuthenticationToken) authentication;
        UserPasswordLoginDto userPasswordLoginDto = (UserPasswordLoginDto) authenticationToken.getPrincipal();
        /**
         * 根据渠道类型调用不同策略feign接口登录//todo 待优化
         */
        UserPasswordAuthenticationToken token = null;
        if (ChannelTypeEnums.BACKEND.getKey().equals(userPasswordLoginDto.getChannel())) {
            //管理平台登录
            UserLoginDto loginDto = new UserLoginDto();
            loginDto.setUsername(userPasswordLoginDto.getUsername());
            loginDto.setPassword(userPasswordLoginDto.getPassword());
            MessageRes<UserLoginVo> login = authFeignApi.login(loginDto);
            if (!login.isSuccess()) {
                throw new AuzBussinessException(login.getCode(), login.getMsg());
            }
            UserLoginVo userLoginVo = login.getData();
            token = buildAuthentication(userLoginVo, userLoginVo.getId(), userLoginVo.getName(), userLoginVo.getMobile(), userLoginVo.getRoles());
        } else {
            CustomLoginDto dto = mapperFacade.map(userPasswordLoginDto, CustomLoginDto.class);
            dto.setLoginName(userPasswordLoginDto.getUsername());
            dto.setPassword(dto.getPassword().toLowerCase());
            MessageRes<CustomLoginDto> login = adUserFeignApi.login(dto);
            if (!login.isSuccess()) {
                throw new AuzBussinessException(login.getCode(), login.getMsg());
            }
            CustomLoginDto adUserConnDto = login.getData();
            token = buildAuthentication(adUserConnDto, Long.valueOf(adUserConnDto.getLoginName()), adUserConnDto.getLoginName(), adUserConnDto.getLoginName(), null);
        }
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 创建认证TOKEN
     *
     * @param object
     * @param userId
     * @param name
     * @param mobile
     * @return
     */
    private UserPasswordAuthenticationToken buildAuthentication(Object object, Long userId, String name, String mobile, List<RoleVo> roles) {
        JSONObject data = new JSONObject();
        data.put("userInfo", object);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roles)) {
            roles.stream().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode())));
        }
        CustomUserDetail customUserDetail = new CustomUserDetail(userId, name, mobile,authorities);
        UserPasswordAuthenticationToken authenticationToken = new UserPasswordAuthenticationToken(name,authorities, data);
        authenticationToken.setDetails(customUserDetail);
        return authenticationToken;
    }
}
