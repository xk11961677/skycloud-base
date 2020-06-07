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
    private boolean modifyBodyPluginOpen = true;

    /**
     * 日志插件
     */
    private boolean parameterPluginOpen = true;

    /**
     * 是否开启验签插件
     */
    private boolean signPluginOpen = true;

    /**
     * 签名秘钥
     */
    private Map<String, String> signPluginSecret = new ConcurrentHashMap<>();

    /**
     * 某渠道忽略此插件
     */
    private String signPluginCloseChannel;

    /**
     * 不执行此插件的URL 以逗号分割且结尾
     */
    private String signPluginCloseUrl;

    /**
     * 不需要网关签名的路由配置
     */
    private String signPluginCloseRoute;


    /**
     * 是否开启验证时间限制
     */
    private boolean timePluginOpen = true;

    /**
     * 是否开启验证时间限制,单位秒
     */
    private int timePluginLimit = 600;
    /**
     * 某渠道忽略此插件
     */
    private String timePluginCloseChannel;

    /**
     * 不执行此插件的URL 以逗号分割且结尾
     */
    private String timePluginCloseUrl;

    /**
     * 不需要网关签名的路由配置
     */
    private String timePluginCloseRoutes;
}
