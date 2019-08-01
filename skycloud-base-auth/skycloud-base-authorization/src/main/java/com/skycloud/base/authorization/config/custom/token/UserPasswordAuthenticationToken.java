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
