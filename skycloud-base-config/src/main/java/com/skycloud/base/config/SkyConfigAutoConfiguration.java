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
        log.info("skycloud base config starter !!! spring env:{}  env:{}  meta:{}", springActiceEnv, apolloEnv.name(), metaServerDomainName);
    }
}
