package com.skycloud.base.config.registry;

import com.skycloud.base.config.property.CustomConsulProperties;
import com.ecwid.consul.v1.ConsulClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;

/**
 * 防止多个无效实例
 *
 * @author
 */
@Slf4j
public class CustomConsulServiceRegistry extends ConsulServiceRegistry {

    private CustomConsulProperties properties;

    public CustomConsulServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties, TtlScheduler ttlScheduler, HeartbeatProperties heartbeatProperties
            , CustomConsulProperties customConsulProperties) {
        super(client, properties, ttlScheduler, heartbeatProperties);
        this.properties = customConsulProperties;
    }

    @Override
    public void register(ConsulRegistration reg) {
        log.info("skycloud base consul registry instance custom:{}", properties.getRegistry().getInstance());
        if (properties.getRegistry().getInstance()) {
            reg.getService().setId(reg.getService().getName() + "-" + reg.getService().getAddress() + "-" + reg.getService().getPort());
        }
        super.register(reg);
    }
}
