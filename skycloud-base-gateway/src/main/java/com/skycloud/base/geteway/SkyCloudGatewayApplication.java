package com.skycloud.base.geteway;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.sky.framework.common.LogUtil;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 */
@RestController
@EnableFeignClients(basePackages = "com.skycloud")
@ComponentScan(basePackages = "com.skycloud")
@Slf4j
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class})
public class SkyCloudGatewayApplication {


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SkyCloudGatewayApplication.class, args);
    }


    @RequestMapping(value = "/defalutFallback")
    @ResponseBody
    public MessageRes fallBackController() {
        LogUtil.info(log, "gateway default hystrix");
        return MessageRes.fail(FailureCodeEnum.GL990002.getCode(), FailureCodeEnum.GL990002.getMsg());
    }

}
