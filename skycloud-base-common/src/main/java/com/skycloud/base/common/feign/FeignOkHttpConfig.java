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
//package com.skycloud.base.common.feign;
//
//import com.sky.framework.model.util.UserContextHolder;
//import feign.Feign;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.ConnectionPool;
//import okhttp3.Interceptor;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author
// */
//@Configuration
//@ConditionalOnClass(Feign.class)
//@AutoConfigureBefore(CustomFeignAutoConfiguration.class)
//@Slf4j
//public class FeignOkHttpConfig {
//    /**
//     * 用户token
//     */
//    public static final String X_CLIENT_TOKEN_USER = "x-client-token-user";
//
//    /**
//     * 服务之间鉴权token
//     */
//    public static final String X_CLIENT_TOKEN = "x-client-token";
//
//    @Bean
//    public okhttp3.OkHttpClient okHttpClient() {
//        return new okhttp3.OkHttpClient.Builder()
//                .readTimeout(60, TimeUnit.SECONDS)
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(120, TimeUnit.SECONDS)
//                .connectionPool(new ConnectionPool())
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        String userToken = UserContextHolder.getInstance().getContext().toString();
//                        log.debug("FeignOkHttpConfig user token :{}"+userToken);
//                        Request.Builder builder = chain.request().newBuilder().addHeader(X_CLIENT_TOKEN_USER, userToken);
//                        return chain.proceed(builder.build());
//                    }
//                })
//                .build();
//    }
//}
