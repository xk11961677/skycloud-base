package com.skycloud.base.geteway.exception;

import com.sky.framework.model.enums.FailureCodeEnum;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局自定义异常处理
 *
 * @author
 */
public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {

    public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        int errCode = FailureCodeEnum.GL999999.getCode();
        int code = 500;
        Throwable error = super.getError(request);
        if (error instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            errCode = FailureCodeEnum.GL990004.getCode();
            code = 404;
        }
        if(error instanceof ResponseStatusException) {
            code = ((ResponseStatusException) error).getStatus().value();
        }
        if(code == HttpStatus.NOT_FOUND.value()) {
            errCode = FailureCodeEnum.GL990004.getCode();
        }
        if(code == HttpStatus.BAD_REQUEST.value()) {
            errCode = FailureCodeEnum.GL990003.getCode();
        }
        return response(code,errCode, this.buildMessage(request, error));
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
        if (map.get("status") != null) {
            statusCode = Integer.parseInt(ObjectUtils.toString(map.get("status")));
            map.remove("status");
        }

        return HttpStatus.valueOf(statusCode);
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
    public Map<String, Object> response(int status, int errCode,String errorMessage) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", errCode);
        map.put("msg", errorMessage);
        map.put("status", status);
        return map;
    }

}
