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

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.sky.framework.model.exception.BusinessException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;


/**
 * 1. 自定义异常不进入熔断
 * 2. 400 - 500(不包含) 不进入熔断
 * 3. sql错误等需要进入熔断
 *
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
            //todo 待优化返回详细信息
            return new HystrixBadRequestException(" request exception status:{}" + response.status());
        }
        try {
            HashMap map = JSON.parseObject(response.body().asInputStream(), HashMap.class);
            String code = ObjectUtils.toString(map.get("code"));
            String message = ObjectUtils.toString(map.get("msg"));
            if (code != null) {
                throw new BusinessException(code, message);
            }
        } catch (IOException e) {
            //防御性容错
            log.info("Failed to process response body", e.getMessage());
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
