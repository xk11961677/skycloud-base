package com.skycloud.base.authorization.config.custom.handler;

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtil;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 */
@Slf4j
public class CustomLoginFailHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        LogUtil.error(log, "登录失败:{}", exception.getMessage());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        String resp = JSON.toJSONString(MessageRes.fail(FailureCodeEnum.AUZ100001.getCode(), FailureCodeEnum.AUZ100001.getMsg()));
        response.getWriter().write(resp);
    }

}
