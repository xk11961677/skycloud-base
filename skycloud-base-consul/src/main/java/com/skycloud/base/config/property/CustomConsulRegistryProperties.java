package com.skycloud.base.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author
 */

@Data
public class CustomConsulRegistryProperties {

    private Boolean instance = true;

}
