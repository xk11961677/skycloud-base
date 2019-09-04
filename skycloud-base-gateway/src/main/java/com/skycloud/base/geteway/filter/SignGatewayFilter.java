/*
 * The MIT License (MIT)
 * Copyright © 2019 <reach>
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
import com.sky.framework.common.encrypt.DefaultMd5Verifier;
import com.sky.framework.model.dto.MessageReq;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.skycloud.base.geteway.common.GatewayConstants;
import com.skycloud.base.geteway.common.ValidationResult;
import com.skycloud.base.geteway.common.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


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
public class SignGatewayFilter implements GlobalFilter, Ordered {

    private static final String GW_SIGN_STATUS = "gw_sign_status";

    private static final String GW_PARAMETERS_ERROR = "gw_parameters_error";

    private static final String GW_SIGN_TIMESTAMP_STATUS = "gw_sign_timestamp_status";

    private static final String DEFAULT_SECRET = "123456";

    private static final String CLIENT_ID = "clientId";

    /**
     * 是否开启验签
     */
    @Value("${gw_sign_open:true}")
    private boolean GW_SIGN_OPEN;

    /**
     * 是否开启验证时间限制
     */
    @Value("${gw_sign_timestamp_open:true}")
    private boolean GW_SIGN_TIMESTAMP_OPEN;

    /**
     * 是否开启验证时间限制,单位秒
     */
    @Value("${gw_sign_timestamp_limit:600}")
    private int GW_SIGN_TIMESTAMP_LIMIT;

    /**
     * 是否开启此插件
     */
    @Value("${gw_sign_plugin_open:true}")
    private boolean GW_SIGN_PLUGIN_OPEN;

    /**
     * 签名秘钥
     */
    @Value("#{${gw_sign_plugin_secret}}")
    private Map<String, String> CLIENT_SECRET = new ConcurrentHashMap<>();

    /**
     * 不执行此插件的URL 以逗号分割且结尾
     */
    @Value("${gw_sign_plugin_close_url}")
    private String GW_SIGN_PLUGIN_CLOSE_URL;

    /**
     * 默认签名验证器
     */
    private static final DefaultMd5Verifier defaultMd5Verifier = new DefaultMd5Verifier();

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
        if (!GW_SIGN_PLUGIN_OPEN) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        URI requestUri = request.getURI();
        if (StringUtils.isNotEmpty(GW_SIGN_PLUGIN_CLOSE_URL)
                && GW_SIGN_PLUGIN_CLOSE_URL.indexOf(requestUri.getPath() + ",") != -1) {
            return chain.filter(exchange);
        }
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        MediaType mediaType = httpHeaders.getContentType();
        String method = exchange.getRequest().getMethodValue();

        if (checkSignAndModifyBody(method, mediaType)) {
            HttpHeaders headers = new HttpHeaders();
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap((body) -> {
                MessageReq messageReq = JSON.parseObject(body, MessageReq.class);
                ValidationResult validate = ValidationUtils.validate(messageReq);

                if (!validate.isHasErrors()) {
                    boolean verifyLimit = doCheckTimeLimit(headers, messageReq);
                    String info = "";
                    if (verifyLimit) {
                        boolean verify = doCheckSign(headers, messageReq);
                        info = verify ? decodeParamters(messageReq) : "";
                    }
                    return Mono.just(info);
                }
                //执行到此处证明参数错误
                List<String> errors = validate.getErrorMsg();
                headers.set(GW_PARAMETERS_ERROR, !CollectionUtils.isEmpty(errors) ? errors.get(0) : FailureCodeEnum.GL990001.getMsg());
                return Mono.just(body);
            });
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            headers.putAll(exchange.getRequest().getHeaders());
            //删除原先内容长度
            headers.remove(GatewayConstants.CONTENT_LENGTH);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public HttpHeaders getHeaders() {
                        long contentLength = headers.getContentLength();
                        HttpHeaders httpHeaders = new HttpHeaders();
                        HttpHeaders header = super.getHeaders();
                        httpHeaders.putAll(header);
                        //重新将内容长度存入header
                        if (contentLength > 0L) {
                            httpHeaders.setContentLength(contentLength);
                        } else {
                            httpHeaders.set("Transfer-Encoding", "chunked");
                        }
                        return httpHeaders;
                    }

                    @Override
                    public Flux<DataBuffer> getBody() {
                        return outputMessage.getBody();
                    }
                };
                String parametersError = headers.getFirst(GW_PARAMETERS_ERROR);
                if (!StringUtils.isEmpty(parametersError)) {
                    return response(exchange, FailureCodeEnum.GL990001.getCode(), parametersError);
                }

                String verifyLimit = headers.getFirst(GW_SIGN_TIMESTAMP_STATUS);
                if (!StringUtils.equals(verifyLimit, "true")) {
                    headers.remove(GW_SIGN_TIMESTAMP_STATUS);
                    return response(exchange, FailureCodeEnum.GL990008.getCode(), FailureCodeEnum.GL990008.getMsg());
                }

                String verify = headers.getFirst(GW_SIGN_STATUS);
                if (StringUtils.equals(verify, "true")) {
                    headers.remove(GW_SIGN_STATUS);
                    return chain.filter(exchange.mutate().request(decorator).build());
                }
                //签名验证没通过
                return response(exchange, FailureCodeEnum.GL990005.getCode(), FailureCodeEnum.GL990005.getMsg());
            }));
        }
        return chain.filter(exchange);
    }


    /**
     * 检测是否验证签名与修改请求体
     * 验证post请求 且 json 请求
     *
     * @param method
     * @param mediaType
     * @return
     */
    private boolean checkSignAndModifyBody(String method, MediaType mediaType) {
        return GatewayConstants.METHOD_POST.equalsIgnoreCase(method) && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType);
    }

    /**
     * 验证签名
     *
     * @param headers
     * @param messageReq
     * @return
     */
    private boolean doCheckSign(HttpHeaders headers, MessageReq messageReq) {
        headers.remove(GW_SIGN_STATUS);
        String clientId = messageReq.getClientId();
        headers.set(CLIENT_ID, clientId);
        boolean verify = true;
        if (GW_SIGN_OPEN) {
            String secret = CLIENT_SECRET.getOrDefault(clientId, DEFAULT_SECRET);
            verify = defaultMd5Verifier.verify(messageReq, secret);
        }
        headers.set(GW_SIGN_STATUS, verify + "");
        return verify;
    }

    /**
     * 检查时间限制
     *
     * @param headers
     * @param messageReq
     * @return
     */
    private boolean doCheckTimeLimit(HttpHeaders headers, MessageReq messageReq) {
        boolean verify = true;
        if (GW_SIGN_TIMESTAMP_OPEN) {
            headers.remove(GW_SIGN_TIMESTAMP_STATUS);
            Long timestamp = Long.valueOf(messageReq.getTimestamp());
            verify = (timestamp > System.currentTimeMillis() - GW_SIGN_TIMESTAMP_LIMIT * 1000) ? true : false;
        }
        headers.set(GW_SIGN_TIMESTAMP_STATUS, verify + "");
        return verify;
    }

    /**
     * url解码
     *
     * @param messageReq
     * @return
     */
    private String decodeParamters(MessageReq messageReq) {
        String param = JSON.toJSONString(messageReq.getParam());
        try {
            param = URLDecoder.decode(param, "UTF-8");
            if (param.startsWith("\"") && param.endsWith("\"")) {
                param = param.substring(1, param.length() - 1);
            }
        } catch (Exception e) {
            LogUtils.error(log, "url decoder exception:{}", e);
        }
        return param;
    }

    private Map<String, Object> decodeBody(String body) {
        return Arrays.stream(body.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    private String encodeBody(Map<String, Object> map) {
        return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
    }

    /**
     * 验证失败
     *
     * @param
     */
    private Mono<Void> response(ServerWebExchange serverWebExchange, Integer code, String desc) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
        HttpHeaders headers = serverWebExchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(response(code, desc));
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
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

}
