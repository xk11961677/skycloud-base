package com.skycloud.base.authorization.config.custom.provider;

import com.alibaba.fastjson.JSONObject;
import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.authorization.config.custom.CustomUserDetail;
import com.skycloud.base.authorization.config.custom.token.SmsCodeAuthenticationToken;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.skycloud.base.authorization.model.dto.MobileLoginDto;
import com.skycloud.service.member.api.model.dto.CustomLoginDto;
import com.skycloud.service.member.api.service.AdUserFeignApi;
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
