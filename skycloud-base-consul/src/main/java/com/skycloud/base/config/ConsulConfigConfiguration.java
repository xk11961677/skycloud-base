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
@ComponentScan(basePackageClasses = ConsulConfigConfiguration.class)
@EnableDiscoveryClient
@ConditionalOnProperty(value = "spring.cloud.service-registry.enabled", matchIfMissing = true)
@AutoConfigureBefore(ServiceRegistryAutoConfiguration.class)
@EnableConfigurationProperties(CustomConsulProperties.class)
@Slf4j
public class ConsulConfigConfiguration implements CommandLineRunner {


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
        log.info("skycloud base consul starter !!! ");
    }
}
