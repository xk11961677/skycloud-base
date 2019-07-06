package com.skycloud.base.geteway.filter;

import com.alibaba.fastjson.JSON;
import com.skycloud.base.geteway.common.ValidationResult;
import com.skycloud.base.geteway.common.ValidationUtils;
import com.sky.framework.common.LogUtil;
import com.sky.framework.common.encrypt.DefaultMd5Verifier;
import com.sky.framework.model.dto.MessageReq;
import com.sky.framework.model.enums.FailureCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author
 * @desc GET  仅验证签名
 * POST 验证签名,并将clientId存入header且入参由messageReq改为messageReq.getParam()中值
 * 上传暂不处理
 * @see org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
 */
//@Component
@Slf4j
@SuppressWarnings("all")
public class SignGatewayFilter implements GlobalFilter, Ordered {

    private static final String METHOD_GET = "GET";

    private static final String METHOD_POST = "POST";

    private static final String GW_SIGN_STATUS = "gw_sign_status";

    private static final String GW_PARAMETERS_ERROR = "gw_parameters_error";

    private static final DefaultMd5Verifier defaultMd5Verifier = new DefaultMd5Verifier();


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        MediaType mediaType = httpHeaders.getContentType();
        String method = exchange.getRequest().getMethodValue();

        //上传与get请求排除
        if (METHOD_GET.equalsIgnoreCase(method)) {
            MultiValueMap<String, String> map = serverRequest.queryParams();
            LogUtil.debug(log, "get请求参数:{}", map);
            return chain.filter(exchange);
        } else if (modifyByPost(method, mediaType)) {
            HttpHeaders headers = new HttpHeaders();
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap((body) -> {
                MessageReq messageReq = JSON.parseObject(body, MessageReq.class);
                ValidationResult validate = ValidationUtils.validate(messageReq);

                if (!validate.isHasErrors()) {
                    headers.remove(GW_SIGN_STATUS);
                    headers.set("clientId", messageReq.getClientId());
                    boolean verify = defaultMd5Verifier.verify(messageReq, "123456");
                    headers.set(GW_SIGN_STATUS, verify + "");
                    String info = decodeParam(messageReq);
                    return Mono.just(info);
                } else {
                    List<String> errors = validate.getErrorMsg();
                    headers.set(GW_PARAMETERS_ERROR, !CollectionUtils.isEmpty(errors) ? errors.get(0) : FailureCodeEnum.GL990001.getMsg());
                }
                return Mono.just(body);
            });
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            headers.putAll(exchange.getRequest().getHeaders());
            //删除原先内容长度
            headers.remove("Content-Length");
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
                String paramError = ObjectUtils.toString(headers.get(GW_PARAMETERS_ERROR));

                if (!StringUtils.isEmpty(paramError)) {
                    return paramError(exchange, paramError);
                }
                String verify = ObjectUtils.toString(headers.get(GW_SIGN_STATUS));
                if (StringUtils.equals(verify, "[true]")) {
                    headers.remove(GW_SIGN_STATUS);
                    return chain.filter(exchange.mutate().request(decorator).build());
                }
                //签名验证没通过
                return unsign(exchange);
            }));
        }
        return chain.filter(exchange);
    }


    /**
     * 验证post请求 且 json 请求
     *
     * @param method
     * @param mediaType
     * @return
     */
    public boolean modifyByPost(String method, MediaType mediaType) {
        return METHOD_POST.equalsIgnoreCase(method) && MediaType.APPLICATION_JSON.isCompatibleWith(mediaType);
    }

    private String decodeParam(MessageReq messageReq) {
        String param = JSON.toJSONString(messageReq.getParam());
        try {
            param = URLDecoder.decode(param, "UTF-8");
            if(param.startsWith("\"") && param.endsWith("\"")) {
                param = param.substring(1,param.length()-1);
            }
        } catch (Exception e) {
            LogUtil.error(log, "url decoder exception:{}", e);
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
     * @param serverWebExchange
     * @return
     */
    private Mono<Void> paramError(ServerWebExchange serverWebExchange, String info) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
        HttpHeaders headers = serverWebExchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(response(FailureCodeEnum.GL990001.getCode(), info.substring(1, info.length() - 1)));
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 验证签名失败
     *
     * @param
     */
    private Mono<Void> unsign(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
        HttpHeaders headers = serverWebExchange.getResponse().getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(response(FailureCodeEnum.GL990005.getCode(), FailureCodeEnum.GL990005.getMsg()));
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


