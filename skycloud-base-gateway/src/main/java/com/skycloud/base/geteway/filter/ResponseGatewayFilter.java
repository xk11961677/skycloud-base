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

import com.skycloud.base.geteway.common.CustomCachedBodyOutputMessage;
import com.skycloud.base.geteway.common.CustomDefaultClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * @author
 */
@Component
@Slf4j
public class ResponseGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return processResponse(exchange, chain);
    }

    private Mono<Void> processResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);
                ResponseAdapter responseAdapter = new ResponseAdapter(body, httpHeaders);

                CustomDefaultClientResponse clientResponse = new CustomDefaultClientResponse(responseAdapter, ExchangeStrategies.withDefaults(), "");
                Mono<String> rawBody = clientResponse.bodyToMono(String.class).map(s -> s);
                BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(rawBody, String.class);
                CustomCachedBodyOutputMessage outputMessage = new CustomCachedBodyOutputMessage(exchange, exchange.getResponse().getHeaders());
                return bodyInserter.insert(outputMessage, new BodyInserterContext())
                        .then(Mono.defer(() -> {
                            Flux<DataBuffer> messageBody = outputMessage.getBody();
                            Flux<DataBuffer> flux = messageBody.map(buffer -> {
                                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
                                DataBufferUtils.release(buffer);
                                // 将响应信息转化为字符串
                                String responseStr = charBuffer.toString();
                                log.info("response info :{} ", responseStr);
                                return getDelegate().bufferFactory().wrap(responseStr.getBytes(StandardCharsets.UTF_8));
                            });
                            HttpHeaders headers = getDelegate().getHeaders();
                            // 修改响应包的大小，不修改会因为包大小不同被浏览器丢掉
                            flux = flux.doOnNext(data -> headers.setContentLength(data.readableByteCount()));
                            return getDelegate().writeWith(flux);
                        }));
            }
        };
        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    private class ResponseAdapter implements ClientHttpResponse {

        private final Flux<DataBuffer> flux;
        private final HttpHeaders headers;

        @SuppressWarnings("unchecked")
        private ResponseAdapter(Publisher<? extends DataBuffer> body, HttpHeaders headers) {
            this.headers = headers;
            if (body instanceof Flux) {
                flux = (Flux) body;
            } else {
                flux = ((Mono) body).flux();
            }
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return flux;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public HttpStatus getStatusCode() {
            return null;
        }

        @Override
        public int getRawStatusCode() {
            return 0;
        }

        @Override
        public MultiValueMap<String, ResponseCookie> getCookies() {
            return null;
        }
    }
}
