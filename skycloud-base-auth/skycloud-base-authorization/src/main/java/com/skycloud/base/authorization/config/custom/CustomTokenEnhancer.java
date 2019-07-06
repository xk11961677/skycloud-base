package com.skycloud.base.authorization.config.custom;

import com.skycloud.base.authorization.config.custom.token.CustomAuthenticationToken;
import com.google.common.collect.Maps;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

/**
 * 自定义token携带内容
 * @author
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = Maps.newHashMap();
        Authentication userAuthentication = authentication.getUserAuthentication();
        if(userAuthentication != null && userAuthentication instanceof CustomAuthenticationToken) {
            CustomAuthenticationToken token = (CustomAuthenticationToken)userAuthentication;
            CustomUserDetail customUserDetail = (CustomUserDetail)token.getDetails();
            additionalInfo.put("user_id", customUserDetail.getUserId());
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}