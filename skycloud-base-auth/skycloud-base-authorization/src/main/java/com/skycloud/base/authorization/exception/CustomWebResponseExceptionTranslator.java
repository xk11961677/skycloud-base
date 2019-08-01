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


