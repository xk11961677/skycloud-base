package com.skycloud.base.common.feign;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.model.exception.BusinessException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;


/**
 * 1. 自定义异常不进入熔断
 * 2. 400 - 500(不包含) 不进入熔断
 * 3. sql错误等需要进入熔断
 * @author
 */
@Slf4j
public class CustomFeignErrorInterceptor implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    /**
     * Decode exception.
     *
     * @param methodKey the method key
     * @param response  the response
     * @return the exception
     */
    @Override
    public Exception decode(final String methodKey, final Response response) {
        if (response.status() >= HttpStatus.BAD_REQUEST.value() && response.status() < HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new HystrixBadRequestException(" request exception status="+response.status());
        }
        try {
            HashMap map = JSON.parseObject(response.body().asInputStream(), HashMap.class);
            Integer code = (Integer) map.get("code");
            String message = (String) map.get("msg");
            if (code != null) {
                throw new BusinessException(code , message);
            }
        } catch (IOException e) {
            System.out.println("Failed to process response body");
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
