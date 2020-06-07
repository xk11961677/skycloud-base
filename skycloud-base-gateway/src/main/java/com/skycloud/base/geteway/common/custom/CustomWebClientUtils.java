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

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 抄袭org.springframework.web.reactive.function.client.WebClientUtils
 * @author
 */
public class CustomWebClientUtils {

    /**
     * Create a delayed {@link ResponseEntity} from the given response and body.
     */
    public static <T> Mono<ResponseEntity<T>> toEntity(ClientResponse response, Mono<T> bodyMono) {
        return Mono.defer(() -> {
            HttpHeaders headers = response.headers().asHttpHeaders();
            int status = response.rawStatusCode();
            return bodyMono
                    .map(body -> createEntity(body, headers, status))
                    .switchIfEmpty(Mono.fromCallable(() -> createEntity(null, headers, status)));
        });
    }

    /**
     * Create a delayed {@link ResponseEntity} list from the given response and body.
     */
    public static <T> Mono<ResponseEntity<List<T>>> toEntityList(ClientResponse response, Publisher<T> body) {
        return Mono.defer(() -> {
            HttpHeaders headers = response.headers().asHttpHeaders();
            int status = response.rawStatusCode();
            return Flux.from(body)
                    .collectList()
                    .map(list -> createEntity(list, headers, status));
        });
    }

    public static <T> ResponseEntity<T> createEntity(@Nullable T body, HttpHeaders headers, int status) {
        HttpStatus resolvedStatus = HttpStatus.resolve(status);
        return resolvedStatus != null
                ? new ResponseEntity<>(body, headers, resolvedStatus)
                : ResponseEntity.status(status).headers(headers).body(body);
    }

}
