package com.skycloud.base.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 授权与认证
 *
 * @author
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.skycloud")
@EnableFeignClients(basePackages = "com.skycloud")
public class Oauth2AuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2AuthorizationApplication.class, args);
    }
}
