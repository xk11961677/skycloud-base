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

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.sky.framework.common.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author
 * @description apollo动态路由
 */
@Component
@Slf4j
public class GatewayPropertRefresher implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private GatewayProperties gatewayProperties;

    private DynamicRouteServiceImpl dynamicRouteService;

    public GatewayPropertRefresher(DynamicRouteServiceImpl dynamicRouteService, GatewayProperties gatewayProperties) {
        this.dynamicRouteService = dynamicRouteService;
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 监听路由修改
     *
     * @param changeEvent
     */
    @ApolloConfigChangeListener(value = "gateway-route-config.yml")
    public void onChange(ConfigChangeEvent changeEvent) {
        boolean gatewayPropertiesChanged = false;
        for (String changedKey : changeEvent.changedKeys()) {
            if (changedKey.startsWith("spring.cloud.gateway")) {
                gatewayPropertiesChanged = true;
                break;
            }
        }
        if (gatewayPropertiesChanged) {
            GatewayPropertRefresher(changeEvent);
        }
    }

    /**
     * 刷新路由信息
     *
     * @param changeEvent
     */
    private void GatewayPropertRefresher(ConfigChangeEvent changeEvent) {
        LogUtils.info(log, "refreshing gateway route!");
        //更新配置
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));

        gatewayProperties.getRoutes().forEach(dynamicRouteService::update);

        LogUtils.info(log, "gateway route refreshed!");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
