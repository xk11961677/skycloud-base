package com.skycloud.base.authentication.api.client.factory;

import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.skycloud.base.authentication.api.client.fallback.AuthFeignFallback;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
public class AuthFeignFallbackFactory implements FallbackFactory<AuthFeignApi> {

    @Override
    public AuthFeignApi create(Throwable throwable) {
        AuthFeignFallback fallback = new AuthFeignFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}
