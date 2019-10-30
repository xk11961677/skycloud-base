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
import com.sky.framework.model.dto.MessageReq;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.skycloud.base.common.constant.BaseConstants;
import com.skycloud.base.geteway.chain.AbstractFilter;
import com.skycloud.base.geteway.chain.Context;
import com.skycloud.base.geteway.chain.FilterChainContext;
import com.skycloud.base.geteway.common.GatewayConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 参数输出过滤器
 *
 * @author
 */
public class TimeFilter extends AbstractFilter {


    @Override
    public void doFilter(Context context) {
        int timePluginLimit = gatewayProperties.getTimePluginLimit();
        FilterChainContext ctx = (FilterChainContext) context;
        ServerHttpRequest request = ctx.getExchange().getRequest();
        String method = request.getMethodValue().toUpperCase();
        String time = "0";
        if (GatewayConstants.METHOD_POST.equals(method)) {
            String body = ctx.getBody();
            MessageReq messageReq = JSON.parseObject(body, MessageReq.class);
            time = messageReq.getTimestamp();
            if (StringUtils.isBlank(time)) {
                time = Optional.ofNullable(request.getQueryParams().getFirst("timestamp")).orElse("0");
            }
        } else if (GatewayConstants.METHOD_GET.equals(method)) {
            time = Optional.ofNullable(request.getQueryParams().getFirst("timestamp")).orElse(time);
        }
        Long timestamp = Long.valueOf(time);
        boolean verify = (timestamp > System.currentTimeMillis() - timePluginLimit * 1000) ? true : false;
        if (!verify) {
            MessageRes result = MessageRes.fail(FailureCodeEnum.GL990008.getCode(), FailureCodeEnum.GL990008.getMsg());
            ctx.setResult(result);
        }
    }

    @Override
    public boolean shouldFilter(Context context) {
        boolean timePluginOpen = gatewayProperties.isTimePluginOpen();
        FilterChainContext ctx = (FilterChainContext) context;
        ServerWebExchange exchange = ctx.getExchange();
        String method = exchange.getRequest().getMethodValue().toUpperCase();
        return timePluginOpen && ctx.getResult().isSuccess() && match(method) && match(exchange);
    }

    private boolean match(String method) {
        return (GatewayConstants.METHOD_POST.equals(method) || GatewayConstants.METHOD_GET.equals(method));
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @SuppressWarnings("all")
    private boolean match(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (ignore(gatewayProperties.getTimePluginIgnoreRoutes(), route.getId())) {
            return false;
        }
        ServerHttpRequest request = exchange.getRequest();
        String channel = request.getHeaders().getFirst(BaseConstants.CHANNEL);
        if (ignore(gatewayProperties.getTimePluginIgnoreChannel(), channel)) {
            return false;
        }
        String path = request.getURI().getPath();
        if (ignore(gatewayProperties.getTimePluginCloseUrl(), path)) {
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
}
