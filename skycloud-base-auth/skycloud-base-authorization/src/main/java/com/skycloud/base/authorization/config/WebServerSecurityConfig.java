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
package com.skycloud.base.authorization.config;

import com.skycloud.base.authorization.config.custom.CustomClientDetailService;
import com.skycloud.base.authorization.config.custom.filter.SmsCodeAuthenticationFilter;
import com.skycloud.base.authorization.config.custom.filter.UserPasswordAuthenticationFilter;
import com.skycloud.base.authorization.config.custom.handler.CustomLoginAuthSuccessHandler;
import com.skycloud.base.authorization.config.custom.handler.CustomLoginFailHandler;
import com.skycloud.base.authorization.config.custom.provider.SmsCodeAuthenticationProvider;
import com.skycloud.base.authorization.config.custom.provider.UserPasswordAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;


/**
 * secuirty 安全配置
 *
 * @author
 */
@Configuration
@EnableWebSecurity
@Slf4j
@SuppressWarnings("all")
public class WebServerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeAuthenticationProvider smsCodeAuthenticationProvider;

    @Autowired
    private UserPasswordAuthenticationProvider userPasswordAuthenticationProvider;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private CustomClientDetailService customClientDetailService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .addFilterBefore(userPasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(smsCodeAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(excludePatterns.toArray(new String[excludePatterns.size()])).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll();
    }

    /**
     * 注入自定义的userDetailsService实现，获取用户信息，设置密码加密方式
     *
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        //自定义短信验证码登录
        authenticationManagerBuilder.authenticationProvider(smsCodeAuthenticationProvider);
        //自定义用户名密码登录
        authenticationManagerBuilder.authenticationProvider(userPasswordAuthenticationProvider);
    }

    /**
     * 将 AuthenticationManager 注册为 bean , 方便配置 oauth server 的时候使用
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 手机验证码认证filter
     *
     * @return
     * @throws Exception
     */
    @Bean
    public SmsCodeAuthenticationFilter smsCodeAuthenticationFilter() throws Exception {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        smsCodeAuthenticationFilter.setCustomClientDetailService(customClientDetailService);
        smsCodeAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(customLoginAuthSuccessHandler());
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailHandler());
        return smsCodeAuthenticationFilter;
    }

    /**
     * 用户名密码认证filter
     *
     * @return
     * @throws Exception
     */
    @Bean
    public UserPasswordAuthenticationFilter userPasswordAuthenticationFilter() throws Exception {
        UserPasswordAuthenticationFilter userPasswordAuthenticationFilter = new UserPasswordAuthenticationFilter();
        userPasswordAuthenticationFilter.setCustomClientDetailService(customClientDetailService);
        userPasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        userPasswordAuthenticationFilter.setAuthenticationSuccessHandler(customLoginAuthSuccessHandler());
        userPasswordAuthenticationFilter.setAuthenticationFailureHandler(customLoginFailHandler());
        return userPasswordAuthenticationFilter;
    }

    /**
     * 创建自定义filter登录成功后处理流程
     *
     * @return
     */
    @Bean
    public CustomLoginAuthSuccessHandler customLoginAuthSuccessHandler() {
        return new CustomLoginAuthSuccessHandler();
    }

    /**
     * 创建自定义filter登录失败后处理流程
     *
     * @return
     */
    @Bean
    public CustomLoginFailHandler customLoginFailHandler() {
        return new CustomLoginFailHandler();
    }

    /**
     * 排除路径
     */
    private List<String> excludePatterns = new ArrayList() {
        {
            add("/");
            add("/*.html");
            add("/favicon.ico");
            add("/**/*.html");
            add("/**/*.css");
            add("/**/*.js");
            add("/swagger-resources/**");
            add("/v2/api-docs");
            add("/actuator/**");

            add("/open/test");
        }
    };
}
