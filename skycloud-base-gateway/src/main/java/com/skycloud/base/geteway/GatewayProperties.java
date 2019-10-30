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
package com.skycloud.base.geteway;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
@ConfigurationProperties(prefix = "gateway")
@Data
public class GatewayProperties {

    /**
     * 是否开启修改请求体插件
     */
    @Value("${gateway.modify_body_plugin_open:true}")
    private boolean modifyBodyPluginOpen;

    /**
     * 日志插件
     */
    @Value("${gateway.parameter_plugin_open:true}")
    private boolean parameterPluginOpen;

    /**
     * 是否开启验签插件
     */
    @Value("${gateway.sign_plugin_open:true}")
    private boolean signPluginOpen;

    /**
     * 签名秘钥
     */
    @Value("#{${gateway.sign_plugin_secret}}")
    private Map<String, String> signClientSecret = new ConcurrentHashMap<>();

    /**
     * 某渠道忽略此插件
     */
    @Value("${gateway.sign_plugin_close_channel:''}")
    private String signPluginIgnoreChannel;

    /**
     * 不执行此插件的URL 以逗号分割且结尾
     */
    @Value("${gateway.sign_plugin_close_url:''}")
    private String signPluginCloseUrl;

    /**
     * 不需要网关签名的路由配置
     */
    @Value("${gateway.sign_plugin_close_route:''}")
    private String signPluginIgnoreRoutes;


    /**
     * 是否开启验证时间限制
     */
    @Value("${gateway.time_plugin_open:true}")
    private boolean timePluginOpen;

    /**
     * 是否开启验证时间限制,单位秒
     */
    @Value("${gateway.time_plugin_limit:600}")
    private int timePluginLimit;
    /**
     * 某渠道忽略此插件
     */
    @Value("${gateway.time_plugin_close_channel:''}")
    private String timePluginIgnoreChannel;

    /**
     * 不执行此插件的URL 以逗号分割且结尾
     */
    @Value("${gateway.time_plugin_close_url:''}")
    private String timePluginCloseUrl;

    /**
     * 不需要网关签名的路由配置
     */
    @Value("${gateway.time_plugin_close_route:''}")
    private String timePluginIgnoreRoutes;
}
