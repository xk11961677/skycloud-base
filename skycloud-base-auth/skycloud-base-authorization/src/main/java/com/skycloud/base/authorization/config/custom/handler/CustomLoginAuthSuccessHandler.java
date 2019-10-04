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
package com.skycloud.base.authorization.config.custom.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.redis.util.RedisTokenUtils;
import com.sky.framework.redis.util.RedisUtils;
import com.skycloud.base.authentication.api.model.vo.UserLoginVo;
import com.skycloud.base.authorization.client.dto.CustomLoginDto;
import com.skycloud.base.authorization.common.Constants;
import com.skycloud.base.authentication.api.model.token.CustomAuthenticationToken;
import com.skycloud.base.common.constant.BaseConstants;
import com.skycloud.base.common.enums.ChannelTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author
 */
@Slf4j
@Data
public class CustomLoginAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            CustomAuthenticationToken customAuthenticationToken = (CustomAuthenticationToken) authentication;
            OAuth2AccessToken oAuth2AccessToken = customAuthenticationToken.getOAuth2AccessToken();
            JSONObject jsonObject = (JSONObject) JSON.toJSON(oAuth2AccessToken);
            String channel = request.getHeader(BaseConstants.CHANNEL);
            if (ChannelTypeEnum.BACKEND.getKey().equals(channel)) {
                UserLoginVo userLoginVo = (UserLoginVo) customAuthenticationToken.getData().get("userInfo");
                jsonObject.put("userInfo", userLoginVo);
            } else {
                CustomLoginDto customLoginDto = (CustomLoginDto) customAuthenticationToken.getData().get("userInfo");
                // 用户当前使用token
                String token;
                // 验证是否已存在token,如果存在则刷新
                String userToken = ObjectUtils.toString(RedisUtils.getString(channel + ":" + String.format(Constants.TOKEN_KEY, customLoginDto.getLoginName())));
                if (StringUtils.isNotEmpty(userToken)) {
                    // 删除原token用户ID
                    RedisUtils.deleteKey(userToken);
                    token = RedisTokenUtils.refreshUserToken(channel, customLoginDto.getLoginName() + "", oAuth2AccessToken.getValue());
                } else {
                    //可能返回原来token
                    token = RedisTokenUtils.getUserToken(channel, customLoginDto.getLoginName() + "", oAuth2AccessToken.getValue());
                }
                if (!token.equals(oAuth2AccessToken.getValue())) {
                    //将jwt生成的token替换成旧系统生成的token
                    jsonObject.put("value", token);
                }
                jsonObject.put("userInfo", customLoginDto);
            }
            LogUtils.info(log, "登录成功:{}" + authentication.getName());
            String resp = JSON.toJSONString(MessageRes.success(jsonObject));
            writer.write(resp);
        } catch (Exception e) {
            LogUtils.error(log, "登录异常", e);
            String result = JSON.toJSONString(MessageRes.fail(FailureCodeEnum.GL999999.getCode(), FailureCodeEnum.GL999999.getMsg()));
            writer.write(result);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
