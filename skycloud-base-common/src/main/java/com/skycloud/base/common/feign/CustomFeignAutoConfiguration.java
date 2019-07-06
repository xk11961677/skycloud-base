package com.skycloud.base.common.feign;

import feign.Feign;
import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author
 */
@Configuration
@ConditionalOnClass(Feign.class)
@Slf4j
public class CustomFeignAutoConfiguration {
    /**
     * 用户token
     */
    public static final String X_CLIENT_TOKEN_USER = "x-client-token-user";

    /**
     * 服务之间鉴权token
     */
    public static final String X_CLIENT_TOKEN = "x-client-token";


    /**
     * Feign logger level logger . level.
     *
     * @return the logger . level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.NONE;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorInterceptor();
    }

    @Bean
    public CustomFeignRequestInterceptor customFeignRequestInterceptor() {
        return new CustomFeignRequestInterceptor();
    }


//    @Bean
//    public okhttp3.OkHttpClient okHttpClient() {
//        return new okhttp3.OkHttpClient.Builder()
//                .readTimeout(60, TimeUnit.SECONDS)
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(120, TimeUnit.SECONDS)
//                .connectionPool(new ConnectionPool())
//                .addInterceptor(chain -> {
//                    String userToken = JSON.toJSONString(UserContextHolder.getInstance().getContext());
//                    log.debug("FeignOkHttpConfig user token :{}" + userToken);
//                    Request.Builder builder = chain.request().newBuilder().addHeader(X_CLIENT_TOKEN_USER, userToken);
//                    return chain.proceed(builder.build());
//                })
//                .build();
//    }
}
