package com.skycloud.base.authorization.swagger;

import com.skycloud.base.authorization.model.dto.MobileLoginDto;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Sets;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.Arrays;

/**
 * 手机号验证码登录
 * @author
 */
@SuppressWarnings("all")
public class SwaggerMobile {

    public static ApiDescription create() {
        return new ApiDescription("groupName",
                "/oauth/mobile",
                "手机号验证码登录",
                Arrays.asList(
                        new OperationBuilder(
                                new CachingOperationNameGenerator())
                                .method(HttpMethod.POST)
                                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                                .consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                                .summary("手机号验证码登录")
                                .notes("手机号验证码登录")
                                .tags(Sets.newHashSet("login"))
                                .parameters(
                                        Arrays.asList(
                                                new ParameterBuilder()
                                                        .description("渠道")
                                                        .type(new TypeResolver().resolve(String.class))
                                                        .name("channel")
                                                        .parameterType("header")
                                                        .parameterAccess("access")
                                                        .required(true)
                                                        .modelRef(new ModelRef("string"))
                                                        .build(),
                                                new ParameterBuilder()
                                                        .description("第三方渠道")
                                                        .type(new TypeResolver().resolve(String.class))
                                                        .name("thirdPartyChannel")
                                                        .parameterType("header")
                                                        .parameterAccess("access")
                                                        .required(false)
                                                        .modelRef(new ModelRef("string"))
                                                        .build(),
                                                new ParameterBuilder()
                                                        .description("appSource")
                                                        .type(new TypeResolver().resolve(String.class))
                                                        .name("appSource")
                                                        .parameterType("header")
                                                        .parameterAccess("access")
                                                        .required(false)
                                                        .modelRef(new ModelRef("string"))
                                                        .build(),
                                                new ParameterBuilder()
                                                        .description("设备号deviceNo")
                                                        .type(new TypeResolver().resolve(String.class))
                                                        .name("deviceNo")
                                                        .parameterType("header")
                                                        .parameterAccess("access")
                                                        .required(false)
                                                        .modelRef(new ModelRef("string"))
                                                        .build(),
                                                new ParameterBuilder()
                                                        .description("设备号deviceId")
                                                        .type(new TypeResolver().resolve(String.class))
                                                        .name("deviceId")
                                                        .parameterType("header")
                                                        .parameterAccess("access")
                                                        .required(false)
                                                        .modelRef(new ModelRef("string"))
                                                        .build(),
                                                new ParameterBuilder()
                                                        .description("mobileLoginDto")
                                                        .type(new TypeResolver().resolve(MobileLoginDto.class))
                                                        .name("mobileLoginDto")
                                                        .parameterType("body")
                                                        .parameterAccess("access")
                                                        .required(true)
                                                        .modelRef(new ModelRef("MobileLoginDto"))
                                                        .build()
                                        ))
                                .build()),
                false);
    }
}
