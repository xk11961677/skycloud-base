package com.skycloud.base.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
@ComponentScan(basePackageClasses = CoreConfiguration.class)
@Slf4j
public class CoreConfiguration {

}
