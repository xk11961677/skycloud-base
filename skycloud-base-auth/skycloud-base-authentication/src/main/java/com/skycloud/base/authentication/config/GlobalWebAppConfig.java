package com.skycloud.base.authentication.config;

import com.sky.framework.web.interceptor.GlobalTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author
 */
@Configuration
public class GlobalWebAppConfig implements WebMvcConfigurer {

    /**
     * 拦截器注入为bean
     *
     * @return
     */
    @Bean
    public HandlerInterceptor getInterceptor() {
        GlobalTokenInterceptor.OPEN_URL.add("/auth" );
        return new GlobalTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns 用于添加拦截规则, 这里假设拦截 /url 后面的全部链接
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(getInterceptor()).addPathPatterns("/**" );
    }
}

