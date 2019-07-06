package com.skycloud.base.authorization.config.custom;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * 预留
 *
 * @author
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

    private static final String BEARER_PRIFIX = "bearer ";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (accessToken instanceof DefaultOAuth2AccessToken) {
            Object principal = authentication.getPrincipal();
//            if (principal instanceof OAuthUser) {
//                OAuthUser user = (OAuthUser) principal;
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("nick_name", user.getUsernickname());
//                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
//            }
        }
        return super.enhance(accessToken, authentication);
    }


    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        return BEARER_PRIFIX + super.encode(accessToken, authentication);
    }
}
