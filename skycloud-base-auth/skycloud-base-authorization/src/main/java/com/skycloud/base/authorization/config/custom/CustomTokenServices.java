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
