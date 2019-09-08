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

import com.skycloud.base.config.property.CustomConsulProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
@ComponentScan(basePackageClasses = ConsulAutoConfiguration.class)
@EnableDiscoveryClient
@ConditionalOnProperty(value = "spring.cloud.service-registry.enabled", matchIfMissing = true)
@AutoConfigureBefore(ServiceRegistryAutoConfiguration.class)
@EnableConfigurationProperties(CustomConsulProperties.class)
@Slf4j
public class ConsulAutoConfiguration implements CommandLineRunner {


//    @Bean
//    @LoadBalanced
//    RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

//    @Autowired
//    private CustomConsulProperties customConsulProperties;
//
//    @Autowired(required = false)
//    private TtlScheduler ttlScheduler;

//    @Bean
//    @Primary
//    public CustomConsulServiceRegistry consulServiceRegistry(ConsulClient consulClient, ConsulDiscoveryProperties properties,
//                                                             HeartbeatProperties heartbeatProperties) {
//        return new CustomConsulServiceRegistry(consulClient, properties, ttlScheduler, heartbeatProperties,customConsulProperties);
//    }

    @Override
    public void run(String... args) {
        log.info("skycloud base consul startup successfully ! ");
    }
}
