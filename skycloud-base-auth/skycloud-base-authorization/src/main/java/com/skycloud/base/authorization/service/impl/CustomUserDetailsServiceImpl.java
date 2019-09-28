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
package com.skycloud.base.authorization.service.impl;

import com.skycloud.base.authorization.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * 扩展security 用户认证
 *
 * @author
 */
@Service("customUserDetailsService")
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

//    @Autowired
//    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return new org.springframework.security.core.userdetails.User(
                username,
                "$2a$10$aoPFu8shqiTcxJvrDA/.MeABbg/bhi8nEfahNYlsNfsBAAZ47cRYa",
                true,
                true,
                true,
                true,
                new HashSet<>());
//        throw new UsernameNotFoundException("Invalid username or password.");
//        LogUtils.info(log, "origin oauth interface :{}", username);
//        User user = userService.getByUsername(username);
//        if(user == null){
//            throw new UsernameNotFoundException("Invalid username or password.");
//        }
//        //password
//        return new org.springframework.security.core.userdetails.User(
//                username,
//                "$2a$10$aoPFu8shqiTcxJvrDA/.MeABbg/bhi8nEfahNYlsNfsBAAZ47cRYa",
//                true,
//                true,
//                true,
//                true,
//                new HashSet<>());
//        return new CustomUserDetail(0L, "admin", "password");
    }

    /**
     * 获得登录者所有角色的权限集合.
     *
     * @param user
     * @return
     */
//    private Set<GrantedAuthority> obtainGrantedAuthorities(User user) {
//        Set<Role> roles = roleService.queryUserRolesByUserId(user.getId());
//        log.info("user:{},roles:{}" , user.getUsername(), roles);
//        return roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.getCode()))
//                .collect(Collectors.toSet());
//    }
}
