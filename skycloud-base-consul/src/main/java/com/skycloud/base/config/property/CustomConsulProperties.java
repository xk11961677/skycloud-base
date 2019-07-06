package com.skycloud.base.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author
 */

@ConfigurationProperties(prefix = "spring.cloud.consul.custom")
@Data
public class CustomConsulProperties {

    private CustomConsulRegistryProperties registry = new CustomConsulRegistryProperties();

}
