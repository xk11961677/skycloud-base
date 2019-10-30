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
package com.skycloud.base.geteway.chain.filter;

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtils;
import com.sky.framework.common.encrypt.DefaultMd5Verifier;
import com.sky.framework.common.encrypt.Verifier;
import com.sky.framework.model.dto.MessageReq;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.skycloud.base.common.constant.BaseConstants;
import com.skycloud.base.geteway.chain.AbstractFilter;
import com.skycloud.base.geteway.chain.Context;
import com.skycloud.base.geteway.chain.FilterChainContext;
import com.skycloud.base.geteway.common.GatewayConstants;
import com.skycloud.base.geteway.common.validation.ValidationResult;
import com.skycloud.base.geteway.common.validation.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 参数输出过滤器
 *
 * @author
 */
@Slf4j
public class SignFilter extends AbstractFilter {

    /**
     * clientId常量
     */
    private static final String CLIENT_ID = "clientId";

    /**
     * 双引号常量
     */
    private static final String SYMBOL = "\"";

    /**
     * 默认秘钥
     */
    private static final String DEFAULT_SECRET = "123456";
    /**
     * 默认签名验证器
     */
    private static final Verifier verifier = new DefaultMd5Verifier();


    @Override
    public void doFilter(Context context) {
        FilterChainContext ctx = (FilterChainContext) context;
        String body = ctx.getBody();
        MessageReq messageReq = JSON.parseObject(body, MessageReq.class);
        ValidationResult validate = ValidationUtils.validate(messageReq);
        MessageRes result = ctx.getResult();
        if (validate.isHasErrors()) {
            List<String> errors = validate.getErrorMsg();
            String msg = !CollectionUtils.isEmpty(errors) ? errors.get(0) : FailureCodeEnum.GL990001.getMsg();
            result.setCode(FailureCodeEnum.GL990001.getCode());
            result.setMsg(msg);
            return;
        }
        HttpHeaders headers = ctx.getHeaders();
        boolean verify = this.verifySignature(headers, messageReq);
        if (!verify) {
            result.setCode(FailureCodeEnum.GL990005.getCode());
            result.setMsg(FailureCodeEnum.GL990005.getMsg());
            return;
        }
        String info = decodeParameters(messageReq);
        result.setData(info);
    }


    @Override
    public boolean shouldFilter(Context context) {
        boolean signPluginOpen = gatewayProperties.isSignPluginOpen();
        FilterChainContext ctx = (FilterChainContext) context;
        ServerWebExchange exchange = ctx.getExchange();
        ServerHttpRequest request = ctx.getExchange().getRequest();
        MediaType mediaType = request.getHeaders().getContentType();
        String method = request.getMethodValue().toUpperCase();
        return signPluginOpen && ctx.getResult().isSuccess() && match(method, mediaType) && match(exchange);
    }

    @Override
    public int getOrder() {
        return 30;
    }

    /**
     * 检测是否验证签名与修改请求体
     * 验证post请求 且 json 请求
     *
     * @param method
     * @param mediaType
     * @return
     */
    private boolean match(String method, MediaType mediaType) {
        return GatewayConstants.METHOD_POST.equals(method)
                && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType);
    }

    @SuppressWarnings("all")
    private boolean match(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (ignore(gatewayProperties.getSignPluginIgnoreRoutes(), route.getId())) {
            return false;
        }
        ServerHttpRequest request = exchange.getRequest();
        String channel = request.getHeaders().getFirst(BaseConstants.CHANNEL);
        if (ignore(gatewayProperties.getSignPluginIgnoreChannel(), channel)) {
            return false;
        }
        String path = request.getURI().getPath();
        if (ignore(gatewayProperties.getSignPluginCloseUrl(), path)) {
            return false;
        }
        return true;
    }


    /**
     * 忽略
     *
     * @param ignores
     * @param target
     * @return
     */
    private boolean ignore(String ignores, String target) {
        return Stream.of(ignores.split(",")).anyMatch(ignore -> ignore.equals(StringUtils.trim(target)));
    }

    /**
     * 验证签名
     *
     * @param headers
     * @param messageReq
     * @return
     */
    private boolean verifySignature(HttpHeaders headers, MessageReq messageReq) {
        String clientId = messageReq.getClientId();
        headers.set(CLIENT_ID, clientId);
        Map<String, String> clientSecret = gatewayProperties.getSignClientSecret();
        String secret = clientSecret.getOrDefault(clientId, DEFAULT_SECRET);
        boolean verify = verifier.verify(messageReq, secret);
        return verify;
    }

    /**
     * url解码
     *
     * @param messageReq
     * @return
     */
    private String decodeParameters(MessageReq messageReq) {
        String param = JSON.toJSONString(messageReq.getParam());
        try {
            param = URLDecoder.decode(param, "UTF-8");
            if (param.startsWith(SYMBOL) && param.endsWith(SYMBOL)) {
                param = param.substring(1, param.length() - 1);
            }
        } catch (Exception e) {
            LogUtils.error(log, "url decoder exception:{}", e);
        }
        return param;
    }
}
