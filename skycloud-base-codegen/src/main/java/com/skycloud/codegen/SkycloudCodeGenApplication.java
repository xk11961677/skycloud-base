package com.skycloud.codegen;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author
 */
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.skycloud")
@ComponentScan(basePackages = "com.skycloud")
public class SkycloudCodeGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkycloudCodeGenApplication.class, args);
	}

}
