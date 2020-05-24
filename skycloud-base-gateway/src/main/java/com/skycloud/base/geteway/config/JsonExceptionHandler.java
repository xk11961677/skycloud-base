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
package com.skycloud.base.geteway.config;


import com.sky.framework.model.enums.SystemErrorCodeEnum;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局自定义异常处理JSON类型
 *
 * @author
 */
public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {

    /**
     * 常量值 status
     */
    private static final String STATUS = "status";

    public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        Throwable error = super.getError(request);
        if (error instanceof NotFoundException || code == HttpStatus.NOT_FOUND.value()) {
            code = HttpStatus.NOT_FOUND.value();
        }
        if (error instanceof ResponseStatusException) {
            code = ((ResponseStatusException) error).getStatus().value();
        }
        String errCode = this.errCodeConverter(code);
        return response(code, errCode, this.buildMessage(request, error));
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     *
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 根据code获取对应的HttpStatus
     *
     * @param map
     */
    @Override
    protected HttpStatus getHttpStatus(Map<String, Object> map) {
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (map.get(STATUS) != null) {
            statusCode = Integer.parseInt(ObjectUtils.toString(map.get(STATUS)));
            map.remove(STATUS);
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 自定义错误码转换
     *
     * @param code
     * @return
     */
    private String errCodeConverter(int code) {
        String errCode = SystemErrorCodeEnum.GL999999.getCode();
        if (code == HttpStatus.BAD_REQUEST.value()) {
            errCode = SystemErrorCodeEnum.GL990003.getCode();
        }
        if (code == HttpStatus.NOT_FOUND.value()) {
            errCode = SystemErrorCodeEnum.GL990004.getCode();
        }
        return errCode;
    }

    /**
     * 构建异常信息
     *
     * @param request
     * @param ex
     * @return
     */
    private String buildMessage(ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }

    /**
     * 构建返回的JSON数据格式
     *
     * @param status       状态码
     * @param errorMessage 异常信息
     * @return
     */
    public Map<String, Object> response(int status, String errCode, String errorMessage) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("code", errCode);
        map.put("msg", errorMessage);
        map.put(STATUS, status);
        return map;
    }

}
