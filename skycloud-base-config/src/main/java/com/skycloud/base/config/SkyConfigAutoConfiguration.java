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
package com.skycloud.base.config;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.ctrip.framework.apollo.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author
 */
@EnableApolloConfig
@Configuration
@ConditionalOnProperty(value = PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, matchIfMissing = true)
@ComponentScan(basePackageClasses = SkyConfigAutoConfiguration.class)
@Slf4j
public class SkyConfigAutoConfiguration implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) {
        String[] defaultProfiles = environment.getDefaultProfiles();
        String springActiceEnv = defaultProfiles[0];
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles != null && activeProfiles.length != 0) {
            springActiceEnv = activeProfiles[0];
        }
        ConfigUtil instance = ApolloInjector.getInstance(ConfigUtil.class);
        String metaServerDomainName = instance.getMetaServerDomainName();
        Env apolloEnv = instance.getApolloEnv();
        apolloEnv = Env.UNKNOWN.name().equalsIgnoreCase(apolloEnv.name())?Env.DEV:apolloEnv;
        log.info("skycloud base config startup successfully ! spring env:{}  env:{}  meta:{}", springActiceEnv, apolloEnv.name(), metaServerDomainName);
    }
}
