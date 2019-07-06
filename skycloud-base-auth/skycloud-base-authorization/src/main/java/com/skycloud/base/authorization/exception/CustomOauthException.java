package com.skycloud.base.authorization.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
class CustomOauthException extends OAuth2Exception {

    private final MessageRes messageRes;

    CustomOauthException(OAuth2Exception oAuth2Exception) {
        super(oAuth2Exception.getSummary(), oAuth2Exception);
        String errorCode = oAuth2Exception.getOAuth2ErrorCode().toUpperCase();
        String summary = oAuth2Exception.getSummary();
        AuthErrorType authErrorType = AuthErrorType.valueOf(errorCode);
        if(authErrorType == null) {
            log.error("CustomOauthException :{}" + oAuth2Exception);
            this.messageRes = MessageRes.fail(FailureCodeEnum.GL999999.getCode(), FailureCodeEnum.GL999999.getMsg());
        }else {
            log.info("authorization fail :{} "+summary);
            this.messageRes = MessageRes.fail(authErrorType.getCode(),authErrorType.getMsg());
        }
    }
}