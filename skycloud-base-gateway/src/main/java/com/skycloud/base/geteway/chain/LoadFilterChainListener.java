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
package com.skycloud.base.geteway.chain;

import com.sky.framework.common.LogUtils;
import com.sky.framework.common.spi.SpiLoader;
import com.skycloud.base.geteway.GatewayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * @author
 */
@Component
@Slf4j
public class LoadFilterChainListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private GatewayProperties gatewayProperties;

    @Value("${spring.application.name}")
    private String name;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            AbstractFilter.gatewayProperties = gatewayProperties;
            ServiceLoader<FilterChain> filterChains = SpiLoader.loadAll(FilterChain.class);
            filterChains.forEach(f -> FilterChainManager.getInstance().add(f));
            LogUtils.info(log, name + "load filter chain successfully:{}");
        } catch (Exception e) {
            LogUtils.error(log, name + "load filter chain failed:{}", e);
        }
    }
}
