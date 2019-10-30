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
import com.sky.framework.model.dto.LogHttpDto;
import com.skycloud.base.geteway.chain.AbstractFilter;
import com.skycloud.base.geteway.chain.Context;
import com.skycloud.base.geteway.chain.FilterChainContext;
import com.skycloud.base.geteway.common.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Optional;

/**
 * 参数输出过滤器
 *
 * @author
 */
@Slf4j
public class ParameterFilter extends AbstractFilter {

    @Override
    public void doFilter(Context context) {
        FilterChainContext ctx = (FilterChainContext) context;
        ServerWebExchange exchange = ctx.getExchange();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        ServerHttpRequest request = exchange.getRequest();
        LogHttpDto logHttpDto = ctx.getLogHttpDto();
        logHttpDto.setQueryString(request.getQueryParams());
        logHttpDto.setUrl(route.getId() + request.getURI().getPath());
        logHttpDto.setQueryString(request.getQueryParams());
        logHttpDto.setBody(ctx.getBody());
        exchange.getAttributes().put("startTime", System.currentTimeMillis());
        LogUtils.info(log, "request parameters :{}", JSON.toJSONString(logHttpDto));
    }

    @Override
    public boolean shouldFilter(Context context) {
        boolean parameterPluginOpen = gatewayProperties.isParameterPluginOpen();
        FilterChainContext ctx = (FilterChainContext) context;
        ServerHttpRequest request = ctx.getExchange().getRequest();
        URI requestUri = request.getURI();
        String schema = requestUri.getScheme();
        String method = request.getMethodValue().toUpperCase();
        String contentType = Optional.ofNullable(request.getHeaders().getFirst(GatewayConstants.CONTENT_TYPE)).orElse("unknown");
        return parameterPluginOpen && (GatewayConstants.HTTP.equals(schema) || GatewayConstants.HTTPS.equals(schema))
                && match(method, contentType);
    }

    @Override
    public int getOrder() {
        return 10;
    }

    private boolean match(String method, String contentType) {
        return (GatewayConstants.METHOD_POST.equals(method) || GatewayConstants.METHOD_GET.equals(method))
                && (!contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE));
    }
}
