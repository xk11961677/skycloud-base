package com.skycloud.base.authentication.api.client.hystrix;

import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
public class AuthFeignHystrix implements AuthFeignApi {
    /**
     * 降级统一返回超时
     *
     * @param authentication
     * @param url
     * @param method
     */
    @Override
    public MessageRes auth(String authentication, String url, String method) {
        return MessageRes.fail(FailureCodeEnum.GL990002.getCode(), FailureCodeEnum.GL990002.getMsg());
    }
}