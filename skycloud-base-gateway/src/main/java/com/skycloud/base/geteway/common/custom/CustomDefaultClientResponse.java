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
package com.skycloud.base.geteway.common.custom;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.Hints;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 抄袭
 * org.springframework.web.reactive.function.client.DefaultClientResponse
 *
 * @author
 */
public class CustomDefaultClientResponse implements ClientResponse {

    private final ClientHttpResponse response;
    private final Headers headers;
    private final ExchangeStrategies strategies;
    private final String logPrefix;

    public CustomDefaultClientResponse(ClientHttpResponse response, ExchangeStrategies strategies, String logPrefix) {
        this.response = response;
        this.strategies = strategies;
        this.headers = new CustomDefaultClientResponse.DefaultHeaders();
        this.logPrefix = logPrefix;
    }

    @Override
    public ExchangeStrategies strategies() {
        return this.strategies;
    }

    @Override
    public HttpStatus statusCode() {
        return this.response.getStatusCode();
    }

    @Override
    public int rawStatusCode() {
        return this.response.getRawStatusCode();
    }

    @Override
    public Headers headers() {
        return this.headers;
    }

    @Override
    public MultiValueMap<String, ResponseCookie> cookies() {
        return this.response.getCookies();
    }

    @Override
    public <T> T body(BodyExtractor<T, ? super ClientHttpResponse> extractor) {
        return extractor.extract(this.response, new BodyExtractor.Context() {
            @Override
            public List<HttpMessageReader<?>> messageReaders() {
                return CustomDefaultClientResponse.this.strategies.messageReaders();
            }

            @Override
            public Optional<ServerHttpResponse> serverResponse() {
                return Optional.empty();
            }

            @Override
            public Map<String, Object> hints() {
                return Hints.from(Hints.LOG_PREFIX_HINT, CustomDefaultClientResponse.this.logPrefix);
            }
        });
    }

    @Override
    public <T> Mono<T> bodyToMono(Class<? extends T> elementClass) {
        return (Mono) this.body(BodyExtractors.toMono(elementClass));
    }

    @Override
    public <T> Mono<T> bodyToMono(ParameterizedTypeReference<T> typeReference) {
        return (Mono) this.body(BodyExtractors.toMono(typeReference));
    }

    @Override
    public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
        return (Flux) this.body(BodyExtractors.toFlux(elementClass));
    }

    @Override
    public <T> Flux<T> bodyToFlux(ParameterizedTypeReference<T> typeReference) {
        return (Flux) this.body(BodyExtractors.toFlux(typeReference));
    }

    @Override
    public <T> Mono<ResponseEntity<T>> toEntity(Class<T> bodyType) {
        return this.toEntityInternal(this.bodyToMono(bodyType));
    }

    @Override
    public <T> Mono<ResponseEntity<T>> toEntity(ParameterizedTypeReference<T> typeReference) {
        return this.toEntityInternal(this.bodyToMono(typeReference));
    }

    private <T> Mono<ResponseEntity<T>> toEntityInternal(Mono<T> bodyMono) {
        HttpHeaders headers = this.headers().asHttpHeaders();
        int status = this.rawStatusCode();
        return bodyMono.map((body) -> {
            return this.createEntity(body, headers, status);
        }).switchIfEmpty(Mono.defer(() -> {
            return Mono.just(this.createEntity(headers, status));
        }));
    }

    @Override
    public <T> Mono<ResponseEntity<List<T>>> toEntityList(Class<T> responseType) {
        return this.toEntityListInternal(this.bodyToFlux(responseType));
    }

    @Override
    public <T> Mono<ResponseEntity<List<T>>> toEntityList(ParameterizedTypeReference<T> typeReference) {
        return this.toEntityListInternal(this.bodyToFlux(typeReference));
    }

    private <T> Mono<ResponseEntity<List<T>>> toEntityListInternal(Flux<T> bodyFlux) {
        HttpHeaders headers = this.headers().asHttpHeaders();
        int status = this.rawStatusCode();
        return bodyFlux.collectList().map((body) -> {
            return this.createEntity(body, headers, status);
        });
    }

    private <T> ResponseEntity<T> createEntity(HttpHeaders headers, int status) {
        HttpStatus resolvedStatus = HttpStatus.resolve(status);
        return resolvedStatus != null ? new ResponseEntity(headers, resolvedStatus) : ((ResponseEntity.BodyBuilder) ResponseEntity.status(status).headers(headers)).build();
    }

    private <T> ResponseEntity<T> createEntity(T body, HttpHeaders headers, int status) {
        HttpStatus resolvedStatus = HttpStatus.resolve(status);
        return resolvedStatus != null ? new ResponseEntity(body, headers, resolvedStatus) : ((ResponseEntity.BodyBuilder) ResponseEntity.status(status).headers(headers)).body(body);
    }

    private class DefaultHeaders implements Headers {
        private DefaultHeaders() {
        }

        private HttpHeaders delegate() {
            return CustomDefaultClientResponse.this.response.getHeaders();
        }

        @Override
        public OptionalLong contentLength() {
            return this.toOptionalLong(this.delegate().getContentLength());
        }

        @Override
        public Optional<MediaType> contentType() {
            return Optional.ofNullable(this.delegate().getContentType());
        }

        @Override
        public List<String> header(String headerName) {
            List<String> headerValues = this.delegate().get(headerName);
            return headerValues != null ? headerValues : Collections.emptyList();
        }

        @Override
        public HttpHeaders asHttpHeaders() {
            return HttpHeaders.readOnlyHttpHeaders(this.delegate());
        }

        private OptionalLong toOptionalLong(long value) {
            return value != -1L ? OptionalLong.of(value) : OptionalLong.empty();
        }
    }
}