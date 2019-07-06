package com.skycloud.base.authorization.exception;

import com.sky.framework.model.dto.MessageRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * @author
 */
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity translate(Exception e) {
        if(e.getCause() != null && e.getCause() instanceof AuzBussinessException) {
            AuzBussinessException auzException = (AuzBussinessException) e.getCause();
            return new ResponseEntity(MessageRes.fail(auzException.getCode(),auzException.getMessage()), HttpStatus.OK);
        }
        if(e instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
//        return ResponseEntity.status(oAuth2Exception.getHttpErrorCode()).body(new CustomOauthException(oAuth2Exception));
            CustomOauthException exception = new CustomOauthException(oAuth2Exception);
            int status = oAuth2Exception.getHttpErrorCode();
            return new ResponseEntity(exception.getMessageRes(), HttpStatus.valueOf(status));
        }
        return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
//    return handleOAuth2Exception(oAuth2Exception);
//    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) {
//
//        int status = e.getHttpErrorCode();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Cache-Control" , "no-store" );
//        headers.set("Pragma" , "no-cache" );
//        if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
//            headers.set("WWW-Authenticate" , String.format("%s %s" , OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
//        }
//
//        CustomOauthException exception = new CustomOauthException(e);
//
//        ResponseEntity<OAuth2Exception> response = new ResponseEntity(exception, headers, HttpStatus.valueOf(status));
//
//        return response;
//
//    }
}


