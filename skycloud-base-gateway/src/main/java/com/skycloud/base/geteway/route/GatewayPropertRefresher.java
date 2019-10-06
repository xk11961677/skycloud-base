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
package com.skycloud.base.geteway.route;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.sky.framework.common.LogUtils;
import com.skycloud.base.nacos.util.NacosUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @author
 * @description 动态路由
 */
@Component
@Slf4j
public class GatewayPropertRefresher implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    private GatewayProperties gatewayProperties;

    private DynamicRouteServiceImpl dynamicRouteService;

    public GatewayPropertRefresher(DynamicRouteServiceImpl dynamicRouteService, GatewayProperties gatewayProperties) {
        this.dynamicRouteService = dynamicRouteService;
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 刷新路由信息
     *
     * @param
     */
    private void gatewayPropertRefresher() {

        try {
            ConfigService configService = NacosUtils.getConfigService(applicationContext);
            String content = configService.getConfig("gateway-route-config.yaml", nacosConfigProperties.getGroup(), nacosConfigProperties.getTimeout());
            configService.addListener("gateway-route-config.yaml", nacosConfigProperties.getGroup(), new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    LogUtils.info(log, "Refreshing gateway properties!");
                    try {
//                        Yaml yaml = new Yaml();
//                        Iterable<Object> objects = yaml.loadAll(configInfo);
//                        List<RouteDefinition> routeDefinitions = JSON.parseArray(configInfo, RouteDefinition.class);
//                        gatewayProperties.getRoutes().forEach(t -> System.out.println(t));
                    } catch (Exception e) {
                        LogUtils.error(log, "gateway properties refreshed exception:{}", e);
                    }
                    LogUtils.info(log, "gateway properties refreshed!");
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        gatewayPropertRefresher();
    }
}
