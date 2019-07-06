package com.skycloud.base.authorization.config.custom.token;

import com.alibaba.fastjson.JSONObject;
import com.skycloud.base.authorization.model.dto.UserPasswordLoginDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * 用户名密码token
 *
 * @author
 */
public class UserPasswordAuthenticationToken extends CustomAuthenticationToken {


    public UserPasswordAuthenticationToken(UserPasswordLoginDto userPasswordLoginDTO) {
        super(null);
        super.setPrincipal(userPasswordLoginDTO);
        this.setAuthenticated(false);
    }

    public UserPasswordAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(principal, authorities);
    }

    public UserPasswordAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, JSONObject data) {
        super(principal, authorities, data);
    }

    public UserPasswordAuthenticationToken(Object principal, JSONObject data) {
        super(principal, new HashSet<>(), data);
    }
}
