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
package com.skycloud.base.config.initializer;

import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.internals.DefaultConfig;
import com.ctrip.framework.apollo.internals.LocalFileConfigRepository;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.skycloud.base.config.property.CustomApolloProperties;
import com.skycloud.base.config.util.TransferUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * 1.将某个属性路径替换成apollo本地缓存路径,即 可支持在apollo配置文件,项目拉取到本地后,自行读取
 * 2.支持获取apollo本地缓存基路径
 *
 * @author
 */
@Slf4j
public class ApolloApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String CUSTOM_APOLLO_BOOTSTRAP_PROPERTY_SOURCES = "CustomApolloBootstrapPropertySources";

    /**
     * 基路径
     */
    public static String BASE_AOPLLO_PATH = "";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.init(applicationContext);
    }

    /**
     * @param applicationContext
     */
    public void init(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        //todo 此处应该再加判断 第一次查看是否使用apollo , 然后再次判断ApolloBootstrapPropertySources
        boolean flag = environment.getPropertySources().contains("ApolloBootstrapPropertySources");
        String properties = environment.getProperty(CustomApolloProperties.REPLACE);
        if (flag && !StringUtils.isEmpty(properties)) {
            try {
                DefaultConfig defaultConfig = (DefaultConfig) ConfigService.getAppConfig();
                Field m_configRepository = defaultConfig.getClass().getDeclaredField("m_configRepository");
                m_configRepository.setAccessible(true);
                LocalFileConfigRepository repository = (LocalFileConfigRepository) ReflectionUtils.getField(m_configRepository, defaultConfig);
                Field m_baseDir = repository.getClass().getDeclaredField("m_baseDir");
                m_baseDir.setAccessible(true);
                File file = (File) ReflectionUtils.getField(m_baseDir, repository);
                BASE_AOPLLO_PATH = file.getAbsolutePath();
                String[] list = file.list();
                ConfigUtil configUtil = ApolloInjector.getInstance(ConfigUtil.class);
                Map<String, Object> props = new HashMap<>();
                String[] names = properties.split(",");
                if (names != null && names.length != 0 && list.length != 0) {
                    for (String name : names) {
                        String[] split = name.split("=");
                        String value = environment.getProperty(split[0]);
                        for (String apolloName : list) {
                            if (apolloName.endsWith(configUtil.getCluster() + "+" + split[1])) {
                                String path = split[2].contains("yml") ? writeYml(BASE_AOPLLO_PATH + "/" + apolloName) : (BASE_AOPLLO_PATH + "/" + apolloName);
                                String newValue = "file:" + path;
                                props.put(split[0], newValue);
                                log.info("extend apollo replace property name:{} oldValue:{} newValue:{}", split[0], value, newValue);
                            }
                        }
                    }
                }
                this.replaceProperty(applicationContext, props);
            } catch (Exception e) {
                log.error("extend apollo init exception:{}", e);
            }
        }
    }


    private void replaceProperty(ConfigurableApplicationContext applicationContext, Map<String, Object> props) {
        if (props.size() != 0) {
            PropertySource propertySource = new MapPropertySource(CUSTOM_APOLLO_BOOTSTRAP_PROPERTY_SOURCES, props);
            applicationContext.getEnvironment().getPropertySources().addLast(propertySource);
        }
    }

    private String writeYml(String path) {
        try {
            String yamlPath = path.replaceAll("properties", "yml");
            TransferUtils.properties2Yaml(path, yamlPath);
            return yamlPath;
        } catch (Exception e) {
            log.error("extend apollo load file has error , path:{} :{}", path, e);
        }
        return null;
    }

}
