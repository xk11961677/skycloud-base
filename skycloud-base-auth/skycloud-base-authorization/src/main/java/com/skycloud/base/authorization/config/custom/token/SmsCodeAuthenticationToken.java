package com.skycloud.base.authorization.config.custom.token;

import com.alibaba.fastjson.JSONObject;
import com.skycloud.base.authorization.model.dto.MobileLoginDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * 短信验证码token
 *
 * @author
 */
public class SmsCodeAuthenticationToken extends CustomAuthenticationToken {


    public SmsCodeAuthenticationToken(MobileLoginDto mobileLoginDTO) {
        super(null);
        super.setPrincipal(mobileLoginDTO);
        this.setAuthenticated(false);
    }

    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(principal, authorities);
    }

    public SmsCodeAuthenticationToken(Object principal, JSONObject data) {
        super(principal, new HashSet<>(), data);
    }

    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, JSONObject data) {
        super(principal, authorities, data);
    }
}
