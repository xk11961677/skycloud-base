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
package com.skycloud.base.authentication.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.redis.util.RedisUtils;
import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.skycloud.base.authentication.api.constant.AuthConstants;
import com.skycloud.base.authentication.api.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthFeignApi authFeignApi;

    /**
     * Authorization认证开头是"bearer "
     */
    private static final int BEARER_BEGIN_INDEX = 7;

    /**
     * jwt token 密钥，主要用于token解析，签名验证
     */
    @Value(AuthConstants.JWT_SIGNING_KEY_VALUE)
    private String signingKey;

    /**
     * 不需要网关签权的url配置(/oauth,/open)
     * 默认/oauth开头是不需要的
     */
    @Value("${gate.ignore.authentication.startWith:/oauth,/open}")
    private String ignoreUrls;
    /**
     * jwt验签
     */
    private MacSigner verifier;

    @Override
    public MessageRes authenticate(String authentication, String url, String method) {
        MessageRes result = authFeignApi.auth(authentication, url, method);
        return result;
    }

    @Override
    public boolean ignoreAuthentication(String url) {
        return Stream.of(this.ignoreUrls.split(",")).anyMatch(ignoreUrl -> url.startsWith(StringUtils.trim(ignoreUrl)));
    }

    @Override
    public boolean hasPermission(MessageRes authResult) {
        return authResult.getCode() == 0 && (boolean) authResult.getData();
    }

    @Override
    public boolean hasPermission(String authentication, String url, String method) {
        //token是否有效(此处非jwt需要修改)
        if (invalidJwtAccessToken(authentication)) {
            return Boolean.FALSE;
        }
        //从认证服务获取是否有权限
        return hasPermission(authenticate(authentication, url, method));
    }

    @Override
    public boolean invalidJwtAccessToken(String authentication) {
        verifier = Optional.ofNullable(verifier).orElse(new MacSigner(signingKey));
        //是否无效true表示无效
        boolean invalid = Boolean.TRUE;

        try {
            Jwt jwt = getJwt(authentication);
            jwt.verifySignature(verifier);
            invalid = Boolean.FALSE;
        } catch (InvalidSignatureException | IllegalArgumentException ex) {
            log.warn("user token has expired or signature error ");
        }
        return invalid;
    }

    @Override
    public Jwt getJwt(String authentication) {
        return JwtHelper.decode(StringUtils.substring(authentication, BEARER_BEGIN_INDEX));
    }

    @Override
    public boolean checkJwtAccessToken(String authentication) {
        boolean invalid = Boolean.FALSE;
        if (!invalidJwtAccessToken(authentication)) {
            String claims = getJwt(authentication).getClaims();
            String exp = ObjectUtils.toString(JSON.parseObject(claims).get("exp"));
            Date expiration = new Date(Long.parseLong(exp) * 1000L);
            invalid = (expiration != null && expiration.after(new Date()));
        }
        return invalid;
    }


    @Override
    public String checkJwtRedis(String authentication) {
        String userId;
        if (authentication.contains("bearer")) {
            authentication = StringUtils.substring(authentication, BEARER_BEGIN_INDEX);
        }
        userId = ObjectUtils.toString(RedisUtils.getString(authentication));
        return userId;
    }

    @Override
    public String getJwtOrNoOld(String authentication) {
        try {
            authentication = getJwt(authentication).getClaims();
        } catch (Exception e) {
            LogUtils.error(log, "getJwtOrNoOld exception:{}", e);
        }
        if (authentication.contains("bearer")) {
            authentication = StringUtils.substring(authentication, BEARER_BEGIN_INDEX);
        }
        return authentication;
    }
}
