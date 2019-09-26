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

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.LogHttpDto;
import com.skycloud.base.geteway.common.CustomCachedBodyOutputMessage;
import com.skycloud.base.geteway.common.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数拦截
 * todo
 * 使用modify body方式修改或者获取请求体,对性能影响较大
 * 考虑将使用 modify body 的filter合并 , 抽取自己的 FilterChain
 *
 * @author
 */
@Component
@Slf4j
public class ParameterGatewayFilter implements GlobalFilter, Ordered {

    private static final Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");

    @Value("${gw_parameter_plugin_open:true}")
    private boolean GW_PARAMETER_PLUGIN_OPEN;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!GW_PARAMETER_PLUGIN_OPEN) {
            return chain.filter(exchange);
        }
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        ServerHttpRequest request = exchange.getRequest();
        URI requestUri = request.getURI();
        String schema = requestUri.getScheme();
        if (!GatewayConstants.HTTP.equals(schema) && !GatewayConstants.HTTPS.equals(schema)) {
            return chain.filter(exchange);
        }

        LogHttpDto logHttpDto = new LogHttpDto();
        logHttpDto.setUrl(route.getId() + requestUri.getPath());
        logHttpDto.setQueryString(request.getQueryParams());
        exchange.getAttributes().put("startTime", System.currentTimeMillis());

        String method = request.getMethodValue();
        String contentType = request.getHeaders().getFirst(GatewayConstants.CONTENT_TYPE);
        if (GatewayConstants.METHOD_GET.equals(method)) {
            return returnMono(chain, exchange, logHttpDto);
        } else if (parameterByPost(method, contentType)) {
            ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap((body) -> {
                logHttpDto.setBody(formatStr(body));
                return Mono.just(body);
            });
            HttpHeaders headers = new HttpHeaders();
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            headers.putAll(exchange.getRequest().getHeaders());
            CustomCachedBodyOutputMessage outputMessage = new CustomCachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return outputMessage.getBody();
                    }
                };
                return returnMono(chain, exchange.mutate().request(decorator).build(), logHttpDto);
            }));
        }
        return chain.filter(exchange);
    }

    private Mono<Void> returnMono(GatewayFilterChain chain, ServerWebExchange exchange, LogHttpDto logHttpDto) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute("startTime");
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                logHttpDto.setExpendTime(executeTime);
                logHttpDto.setHttpCode(Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
                LogUtils.info(log, JSON.toJSONString(logHttpDto));
            }
        }));
    }

    @Override
    public int getOrder() {
        return 10;
    }

    /**
     * 记录post请求参数验证条件
     *
     * @param method
     * @param contentType
     * @return
     */
    private boolean parameterByPost(String method, String contentType) {
        return GatewayConstants.METHOD_POST.equals(method) && (
                StringUtils.isEmpty(contentType) ||
                        !contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE));
    }

    /**
     * 去掉空格,换行和制表符
     *
     * @param str
     * @return
     */
    private String formatStr(String str) {
        if (str != null && str.length() > 0) {
            Matcher m = pattern.matcher(str);
            return m.replaceAll("");
        }
        return str;
    }
}
