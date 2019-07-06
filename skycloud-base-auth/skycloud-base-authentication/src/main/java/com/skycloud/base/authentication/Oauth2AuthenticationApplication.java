package com.skycloud.base.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author
 */
@SpringBootApplication
@MapperScan("com.skycloud.base.authc.server.mapper")
public class Oauth2AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2AuthenticationApplication.class, args);
    }
}

