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
package com.skycloud.base.authentication.api.config;

import com.skycloud.base.authentication.api.constant.AuthConstants;
import com.skycloud.base.authentication.api.enums.TokenStoreTypeEnum;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

/**
 * @author
 */
@Configuration
@ConditionalOnClass(name="org.springframework.security.oauth2.provider.token.TokenStore")
public class TokenStoreConfig {

    /**
     * jwt 对称加密密钥
     */
    @Value(AuthConstants.JWT_SIGNING_KEY_VALUE)
    private String signingKey;

    /**
     * 数据源
     */
    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    @Value("${" + AuthConstants.TOKEN_STORE_TYPE + ":jwt}")
    private String tokenStoreType;

    /**
     * token的持久化
     *
     * @return JwtTokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        TokenStore store;
        switch (TokenStoreTypeEnum.acquire(tokenStoreType)) {
            case JWT:
                store = new JwtTokenStore(jwtAccessTokenConverter());
                break;
            case REDIS:
                if (redisConnectionFactory == null) {
                    throw new BeanCreationException("配置Redis存储Token需要redisConnectionFactory bean，未找到");
                }
                store = new RedisTokenStore(redisConnectionFactory);
                break;
            case JDBC:
                if (dataSource == null) {
                    throw new BeanCreationException("配置jdbc存储Token需要dataSource bean，未找到");
                }
                store = new JdbcTokenStore(dataSource);
                break;
            default:
                store = new InMemoryTokenStore();
        }
        return store;
    }

    /**
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnExpression("'${" + AuthConstants.TOKEN_STORE_TYPE + "}'.equals('jwt')")
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(signingKey);
        return converter;
    }
}
