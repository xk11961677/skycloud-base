//package com.skycloud.base.common.feign;
//
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.alibaba.fastjson.support.config.FastJsonConfig;
//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
//import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;
//import feign.Logger;
//import feign.codec.Decoder;
//import feign.codec.Encoder;
//import org.springframework.beans.factory.ObjectFactory;
//import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
//import org.springframework.cloud.openfeign.FeignLoggerFactory;
//import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
//import org.springframework.cloud.openfeign.support.SpringDecoder;
//import org.springframework.cloud.openfeign.support.SpringEncoder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import springfox.documentation.spring.web.json.Json;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author
// */
//@Configuration
//public class FeignConfig {
//
//    @Bean
//    public Encoder encoderFeignEncoder() {
//        return new SpringEncoder(feignHttpMessageConverter());
//    }
//
//    @Bean
//
//    public Decoder decoderFeignDecoder() {
//        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
//
//    }
//
//    @Bean
//    Logger.Level feignLevel() {
//        return Logger.Level.FULL;
//    }
//
//    @Bean
//    FeignLoggerFactory infoFeignLoggerFactory() {
//        return new InfoFeignLoggerFactory();
//    }
//
//
//    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
//        final HttpMessageConverters httpMessageConverters =
//                new HttpMessageConverters(getFastJsonConverter());
//        return () -> httpMessageConverters;
//    }
//
//    private FastJsonHttpMessageConverter getFastJsonConverter() {
//        FastJsonHttpMessageConverter converter =
//                new FastJsonHttpMessageConverter();
//
//        List<MediaType> supportedMediaTypes = new ArrayList<>();
//        MediaType mediaTypeJson =
//                MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE);
//        supportedMediaTypes.add(mediaTypeJson);
//        converter.setSupportedMediaTypes(supportedMediaTypes);
//        FastJsonConfig config = new FastJsonConfig();
//        config.getSerializeConfig()
//                .put(Json.class, new SwaggerJsonSerializer());
//        config.setSerializerFeatures(
//                SerializerFeature.DisableCircularReferenceDetect);
//        converter.setFastJsonConfig(config);
//
//        return converter;
//    }
//}
