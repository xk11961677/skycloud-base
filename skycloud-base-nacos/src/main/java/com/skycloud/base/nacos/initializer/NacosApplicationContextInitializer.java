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
package com.skycloud.base.nacos.initializer;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.client.NacosPropertySource;
import com.alibaba.nacos.api.config.ConfigService;
import com.sky.framework.common.nacos.NacosContainer;
import com.skycloud.base.nacos.property.CustomNacosProperties;
import com.skycloud.base.nacos.util.ConvertStreamUtils;
import com.skycloud.base.nacos.util.NacosUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author
 */
@Slf4j
public class NacosApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final String NACOS_PROPERTY_SOURCE_NAME = "NACOS";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.init(applicationContext);
    }

    /**
     * @param context
     */
    public void init(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String properties = environment.getProperty(CustomNacosProperties.REPLACE);
        CompositePropertySource compositePropertySource = (CompositePropertySource) environment.getPropertySources().get("bootstrapProperties");
        boolean flag = false;
        if (compositePropertySource != null && !StringUtils.isEmpty(properties)) {
            String[] names = properties.split(",");
            if (names != null && names.length != 0) {
                List<String> list = Arrays.asList(names);
                Collection<PropertySource<?>> propertySources = compositePropertySource.getPropertySources();
                for (Iterator<PropertySource<?>> iterator = propertySources.iterator(); iterator.hasNext(); ) {
                    PropertySource<?> next = iterator.next();
                    if (next.getName().equals(NACOS_PROPERTY_SOURCE_NAME)) {
                        flag = true;
                        NacosConfigProperties nacosConfigProperties = NacosUtils.nacosConfigProperties(context);
                        ConfigService configService = NacosUtils.getConfigService(context);
                        Collection<PropertySource<?>> composite = ((CompositePropertySource) next).getPropertySources();
                        Iterator<PropertySource<?>> iter = composite.iterator();
                        while (iter.hasNext()) {
                            NacosPropertySource nacos = (NacosPropertySource) iter.next();
                            String name = nacos.getName();
                            if (list.contains(name)) {
                                try {
                                    String content = configService.getConfig(nacos.getDataId(), nacos.getGroup(), nacosConfigProperties.getTimeout());
                                    ByteArrayInputStream in = ConvertStreamUtils.parseToInputStream(content);
                                    NacosContainer.put(name, in);
                                    log.info("extend nacos name:{}", name);
                                } catch (Exception e) {
                                    log.error("extend nacos init :{} exception:{}", name, e);
                                }
                            }
                        }
                    }
                }
            }
            log.info("used nacos config:{}", flag);
        }
    }
}
