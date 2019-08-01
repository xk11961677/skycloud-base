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
package com.skycloud.base.authorization.exception;

import com.sky.framework.model.enums.ErrorCode;

/**
 * @author
 */
public enum AuthErrorType implements ErrorCode {

    INVALID_REQUEST(100004, "无效请求"),
    INVALID_CLIENT(100005, "无效client_id"),
    INVALID_GRANT(100006, "无效授权"),
    INVALID_SCOPE(100007, "无效scope"),
    INVALID_TOKEN(100008, "无效token"),
    INSUFFICIENT_SCOPE(100009, "授权不足"),
    REDIRECT_URI_MISMATCH(100010, "redirect url不匹配"),
    ACCESS_DENIED(100003, "无权访问"),
    METHOD_NOT_ALLOWED(100011, "不支持该方法"),
    SERVER_ERROR(100012, "权限服务错误"),
    UNAUTHORIZED_CLIENT(100013, "未授权客户端"),
    UNAUTHORIZED(100001, "未授权"),
    UNSUPPORTED_RESPONSE_TYPE(100014, "不支持的响应类型"),
    UNSUPPORTED_GRANT_TYPE(100015, "不支持的授权类型"),
    AUZ100026(100026, "身份验证失败，请退出重新登录或联系企业管理员，谢谢！"),




    ;

    /**
     * 错误类型码
     */
    private int code;
    /**
     * 错误类型描述信息
     */
    private String msg;

    AuthErrorType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }}
