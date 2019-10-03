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
import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.geteway.GatewayProperties;
import com.skycloud.base.geteway.chain.FilterChainContext;
import com.skycloud.base.geteway.chain.FilterChainManager;
import com.skycloud.base.geteway.common.GatewayConstants;
import com.skycloud.base.geteway.common.custom.CustomCachedBodyOutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import java.util.Objects;
import java.util.Optional;


/**
 * 全局验证签名,后期可改成动态管理
 * <p>
 * GET  不验证签名
 * POST 验证签名,并将clientId存入header且入参由messageReq改为messageReq.getParam()中值
 * 上传暂不处理
 * <p>
 * FUTURES
 * 1.将验签与修改请求体分开
 * 2.秘钥管理,即接入clientId具有管理功能
 *
 * @author
 * @see org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
 * @since
 */
@Component
@Slf4j
public class ModifyBodyGatewayFilter implements GlobalFilter, Ordered {

    @Autowired
    private GatewayProperties gatewayProperties;

    /**
     * 修改参数体
     * <p>
     * 1.仅修改 post && application/json  形式请求体数据
     *
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @author sky
     * @date 2019-08-20 09:46:02
     * @since
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!gatewayProperties.isModifyBodyPluginOpen()) {
            return chain.filter(exchange);
        }
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        HttpHeaders originHeaders = exchange.getRequest().getHeaders();
        LogUtils.info(log, "origin content length :{}", originHeaders.getFirst(GatewayConstants.CONTENT_LENGTH));

        HttpHeaders headers = new HttpHeaders();
        FilterChainContext context = new FilterChainContext();
        context.setExchange(exchange);
        context.setHeaders(headers);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap((body) -> {
            context.setBody(body);
            FilterChainManager.getInstance().doFilter(context);
            MessageRes<String> result = context.getResult();
            String data = Optional.ofNullable(result.isSuccess() ? result.getData() : body).orElse("");
            return Mono.just(data);
        });
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        headers.putAll(originHeaders);
        headers.remove(GatewayConstants.CONTENT_LENGTH);
        CustomCachedBodyOutputMessage outputMessage = new CustomCachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            ServerHttpRequest requestDecorator = requestDecorate(exchange, headers, outputMessage);
            MessageRes result = context.getResult();
            if (result.isSuccess()) {
                return returnMono(chain, exchange.mutate().request(requestDecorator).build(), context.getLogHttpDto());
            }
            return response(exchange, context, result.getCode(), result.getMsg());
        }));
    }

    private Mono<Void> returnMono(GatewayFilterChain chain, ServerWebExchange exchange, LogHttpDto logHttpDto) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> this.printRequestExpendTime(exchange, logHttpDto)));
    }

    /**
     * 验证失败
     *
     * @param
     */
    private Mono<Void> response(ServerWebExchange exchange, FilterChainContext context, Integer code, String desc) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(response(code, desc));
        this.printRequestExpendTime(exchange, context.getLogHttpDto());
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 打印请求耗时与参数
     *
     * @param exchange
     * @param logHttpDto
     */
    private void printRequestExpendTime(ServerWebExchange exchange, LogHttpDto logHttpDto) {
        Long startTime = exchange.getAttribute("startTime");
        Optional.ofNullable(startTime).ifPresent(time -> {
            long executeTime = (System.currentTimeMillis() - time);
            logHttpDto.setExpendTime(executeTime);
            logHttpDto.setHttpCode(Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
            LogUtils.info(log, "request expendTime :{}", JSON.toJSONString(logHttpDto));
        });
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
        return 100;
    }


    ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers, CustomCachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                LogUtils.info(log, "new content length :{}", httpHeaders.getFirst(GatewayConstants.CONTENT_LENGTH));
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }
}


