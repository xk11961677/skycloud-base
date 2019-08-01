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
//package com.skycloud.base.authorization.config.custom;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2RefreshToken;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.transaction.annotation.Transactional;
//
//public class CustomTokenServices extends DefaultTokenServices {
//
//
//    @Override
//    @Transactional
//    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
//        OAuth2AccessToken existingAccessToken = this.getAccessToken(authentication);
//        OAuth2RefreshToken refreshToken = null;
//        if (existingAccessToken != null) {
//            if (!existingAccessToken.isExpired()) {
//                this.tokenStore.storeAccessToken(existingAccessToken, authentication);
//                return existingAccessToken;
//            }
//
//            if (existingAccessToken.getRefreshToken() != null) {
//                refreshToken = existingAccessToken.getRefreshToken();
//                this.tokenStore.removeRefreshToken(refreshToken);
//            }
//
//            this.tokenStore.removeAccessToken(existingAccessToken);
//        }
//
//        if (refreshToken == null) {
//            refreshToken = this.createRefreshToken(authentication);
//        } else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
//            ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
//            if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
//                refreshToken = this.createRefreshToken(authentication);
//            }
//        }
//
//        OAuth2AccessToken accessToken = this.createAccessToken(authentication, refreshToken);
//        this.tokenStore.storeAccessToken(accessToken, authentication);
//        refreshToken = accessToken.getRefreshToken();
//        if (refreshToken != null) {
//            this.tokenStore.storeRefreshToken(refreshToken, authentication);
//        }
//
//        return accessToken;
//    }
//
//
//}
