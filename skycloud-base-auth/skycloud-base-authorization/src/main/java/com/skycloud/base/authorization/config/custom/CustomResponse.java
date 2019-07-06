package com.skycloud.base.authorization.config.custom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import com.sky.framework.model.exception.BusinessException;
import com.sky.framework.common.LogUtil;
import com.sky.framework.model.dto.MessageRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @author
 */
@Slf4j
public class CustomResponse {

    /**
     * 响应自定义错误码
     *
     * @param response
     * @param auze
     */
    public static void response(HttpServletResponse response, AuzBussinessException auze) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            String resp = null;
            JSONObject data = auze.getData();
            if (data != null) {
                data.put("code", auze.getCode());
                data.put("msg", auze.getMessage());
                resp = data.toJSONString();
            } else {
                resp = JSON.toJSONString(MessageRes.fail(auze.getCode(), auze.getMessage()));
            }
            response.getWriter().write(resp);
        } catch (Exception e) {
            LogUtil.error(log, "response msg exception:{}", e.getMessage());
        }
    }


    /**
     * 响应自定义错误码
     *
     * @param response
     * @param auze
     */
    public static void response(HttpServletResponse response, BusinessException auze) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            String resp = JSON.toJSONString(MessageRes.fail(auze.getCode(), auze.getMessage()));
            response.getWriter().write(resp);
        } catch (Exception e) {
            LogUtil.error(log, "response msg exception:{}", e.getMessage());
        }
    }


    public static void response(HttpServletResponse response, String content) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(content);
        } catch (Exception e) {
            LogUtil.error(log, "response msg exception:{}", e.getMessage());
        }
    }
}
