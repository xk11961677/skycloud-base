package com.skycloud.base.authentication.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.skycloud.base.authentication.api.client.AuthFeignApi;
import com.skycloud.base.authentication.api.service.AuthService;
import com.sky.framework.common.LogUtil;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Value("${spring.security.oauth2.jwt.signingKey}")
    private String signingKey;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 不需要网关签权的url配置(/oauth,/open)
     * 默认/oauth开头是不需要的
     */
    @Value("${gate.ignore.authentication.startWith}")
    private String ignoreUrls = "/oauth";
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
        //token是否有效
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
        userId = ObjectUtils.toString(redisUtil.getString(authentication));
        return userId;
    }

    @Override
    public String getJwtOrNoOld(String authentication) {
        try {
            authentication = getJwt(authentication).getClaims();
        } catch (Exception e) {
            LogUtil.error(log, "getJwtOrNoOld exception:{}", e);
        }
        if (authentication.contains("bearer")) {
            authentication = StringUtils.substring(authentication, BEARER_BEGIN_INDEX);
        }
        return authentication;
    }
}
