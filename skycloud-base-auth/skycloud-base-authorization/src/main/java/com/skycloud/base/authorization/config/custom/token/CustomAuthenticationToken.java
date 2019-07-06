package com.skycloud.base.authorization.config.custom.token;

import com.alibaba.fastjson.JSONObject;
import com.skycloud.base.authorization.model.dto.MobileLoginDto;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author
 */
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;

    @Getter
    private JSONObject data;

    public CustomAuthenticationToken(MobileLoginDto mobileLoginDTO) {
        super(null);
        this.principal = mobileLoginDTO;
        this.setAuthenticated(false);
    }

    public CustomAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public CustomAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, JSONObject data) {
        super(authorities);
        this.principal = principal;
        this.data = data;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }

}
