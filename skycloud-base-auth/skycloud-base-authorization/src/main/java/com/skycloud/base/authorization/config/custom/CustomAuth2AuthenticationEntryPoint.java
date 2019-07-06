package com.skycloud.base.authorization.config.custom;

import com.alibaba.fastjson.JSON;
import com.skycloud.base.authorization.exception.AuthErrorType;
import com.sky.framework.common.LogUtil;
import com.sky.framework.model.dto.MessageRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.AbstractOAuth2SecurityExceptionHandler;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * //添加自定义异常入口，处理accessdeine异常
 * http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
 * .accessDeniedHandler(new CustomAccessDeineHandler());
 *
 * @author
 */
@Slf4j
public class CustomAuth2AuthenticationEntryPoint extends AbstractOAuth2SecurityExceptionHandler implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        Throwable throwable = authException.getCause();
        MessageRes fail;
        if (throwable != null) {
            String msg = throwable.toString();
            AuthErrorType authErrorType = getAuthErrorType(msg);
            fail = MessageRes.fail(authErrorType.getCode(), authErrorType.getMsg());
        } else {
            String message = authException.getMessage();
            fail = MessageRes.fail(AuthErrorType.UNAUTHORIZED.getCode(), message);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JSON.toJSONString(fail));
    }

    private AuthErrorType getAuthErrorType(String msg) {
        AuthErrorType type = null;
        try {
            String s = msg.split(",")[0].replaceAll("error=", "").replaceAll("\"", "");
            type = AuthErrorType.valueOf(s.toUpperCase());
        } catch (Exception e) {
            LogUtil.error(log, "", e);
        }
        return type == null ? AuthErrorType.UNAUTHORIZED : type;
    }

}