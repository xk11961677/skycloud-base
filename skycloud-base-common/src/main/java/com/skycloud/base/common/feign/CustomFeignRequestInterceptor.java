package com.skycloud.base.common.feign;

import com.alibaba.fastjson.JSON;
import com.sky.framework.model.util.UserContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 添加header信息
 * @author
 */
@Slf4j
public class CustomFeignRequestInterceptor implements RequestInterceptor {

    /**
     * 用户token
     */
    public static final String X_CLIENT_TOKEN_USER = "x-client-token-user";

    /**
     * 服务之间鉴权token
     */
    public static final String X_CLIENT_TOKEN = "x-client-token";

    /**
     * Apply.
     *
     * @param template the template
     */
    @Override
    public void apply(RequestTemplate template) {
        Map<String, String> context = UserContextHolder.getInstance().getContext();
        String token = null;
        if(context != null) {
            token = JSON.toJSONString(context);
        }
        log.debug("CustomFeignRequestInterceptor user token :{}"+token);
        template.header(X_CLIENT_TOKEN_USER, token);
    }
}

