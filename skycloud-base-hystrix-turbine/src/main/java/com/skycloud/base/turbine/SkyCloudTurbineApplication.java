package com.skycloud.base.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 *
 *
 * @author
 */
@SpringBootApplication
@EnableTurbine
@EnableHystrixDashboard
public class SkyCloudTurbineApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SkyCloudTurbineApplication.class, args);
    }
}
