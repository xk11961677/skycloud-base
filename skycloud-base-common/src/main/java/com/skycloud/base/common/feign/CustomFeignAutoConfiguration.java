/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
