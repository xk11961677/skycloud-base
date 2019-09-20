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
package com.skycloud.base.geteway.filter;

import com.sky.framework.common.LogUtils;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.skycloud.base.authentication.api.service.AuthService;
import com.skycloud.base.common.constant.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.stream.Stream;

/**
 * 请求url权限校验
 *
 * @author
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class AccessGatewayFilter implements GlobalFilter, Ordered {

    private static final String X_CLIENT_TOKEN_USER_ID = "user_id";
    private static final String BEARER = "bearer";

    @Resource
    private AuthService authService;

    @Resource
    private AuthFeignApi authFeignApi;

    /**
     * 不需要网关签权的路由配置
     */
    @Value("${gate.ignore.authentication.route:''}")
    private String ignoreRoutes;

    /**
     * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
     * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = (Route) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (ignoreRouteAuthentication(route.getId())) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String channel = request.getHeaders().getFirst(BaseConstants.CHANNEL);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        //不需要网关签权的url
        if (authService.ignoreAuthentication(url)) {
            return chain.filter(exchange);
        }
        // 如果请求未携带token信息, 直接跳出
        if (StringUtils.isBlank(authentication) || !authentication.contains(BEARER)) {
            LogUtils.debug(log, "url:{},method:{},headers:{}, 请求未携带token信息", url, method, request.getHeaders());
            return unauthorized(exchange);
        }

        /**
         *
         * 仅验证 token 是否正确与是否过期
         */
        String userId = authService.checkJwtRedis(authentication);

        if (!StringUtils.isEmpty(userId)) {
            ServerHttpRequest.Builder builder = request.mutate();
            builder.header(X_CLIENT_TOKEN_USER_ID, userId);
            builder.header(BaseConstants.CHANNEL, ObjectUtils.toString(channel));
            //TODO 转发的请求都加上服务间认证token
            builder.header(BaseConstants.X_CLIENT_TOKEN, "TODO 添加服务间简单认证");
            //将jwt token中的用户信息传给服务
            String claims = authService.getJwtOrNoOld(authentication);
            builder.header(BaseConstants.X_CLIENT_TOKEN_USER, claims);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        return unpermission(exchange);
    }

    /**
     * 网关拒绝，返回401
     * HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes()
     *
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        HttpHeaders headers = serverWebExchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(response(FailureCodeEnum.AUZ100001.getCode(), FailureCodeEnum.AUZ100001.getMsg()));
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }


    /**
     * 网关拒绝，返回403
     *
     * @param HttpStatus.FORBIDDEN.getReasonPhrase().getBytes()
     */
    private Mono<Void> unpermission(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        HttpHeaders headers = serverWebExchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(response(FailureCodeEnum.AUZ100016.getCode(), FailureCodeEnum.AUZ100016.getMsg()));
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 获取认证的路由
     *
     * @param route
     * @return
     */
    private boolean ignoreRouteAuthentication(String route) {
        return Stream.of(ignoreRoutes.split(",")).anyMatch(ignoreRoute -> route.equals(StringUtils.trim(ignoreRoute)));
    }

    /**
     * 响应JSON
     *
     * @param code
     * @param desc
     * @return
     */
    private byte[] response(Integer code, String desc) {
        byte[] msg = ("{\"code\":\"" + code + "\",\"msg\":\"" + desc + "\"}").getBytes();
        return msg;
    }

    @Override
    public int getOrder() {
        return 90;
    }
}
