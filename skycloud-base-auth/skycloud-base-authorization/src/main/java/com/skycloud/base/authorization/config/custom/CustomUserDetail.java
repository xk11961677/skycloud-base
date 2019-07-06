package com.skycloud.base.authorization.config.custom;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author
 */
public class CustomUserDetail implements UserDetails {

    private String userName;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    @Getter
    private Long userId;

    public CustomUserDetail(Long userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.authorities = new HashSet<>();
    }

    public CustomUserDetail(Long userId, String userName, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
