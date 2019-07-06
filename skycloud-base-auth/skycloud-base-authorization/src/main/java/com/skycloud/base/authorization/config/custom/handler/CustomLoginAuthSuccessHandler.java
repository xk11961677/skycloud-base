package com.skycloud.base.authorization.config.custom.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sky.framework.common.LogUtil;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.redis.util.RedisTokenUtil;
import com.sky.framework.redis.util.RedisUtil;
import com.skycloud.base.authorization.common.Constants;
import com.skycloud.base.authorization.config.custom.token.CustomAuthenticationToken;
import com.skycloud.service.member.api.model.dto.CustomLoginDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * @author
 */
@Slf4j
@Data
public class CustomLoginAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String HTTP_BASIC = "Basic ";

    private ClientDetailsService clientDetailsService;

    private AuthorizationServerTokenServices authorizationServerTokenServices;

    private PasswordEncoder passwordEncoder;

    private RedisUtil redisUtil;

    private static final String TOKEN_SALT = "12321321324243";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        try{
            //todo 需要转移代码
//            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//            if (header == null || !header.startsWith(HTTP_BASIC)) {
//                throw new UnapprovedClientAuthenticationException("请求头中无client信息");
//            }
//            String[] tokens = extractAndDecodeHeader(header, request);
//            assert tokens.length == 2;
//            String clientId = tokens[0];
//            String clientSecret = tokens[1];
            String clientId = "test_client";
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            if (clientDetails == null) {
                throw new UnapprovedClientAuthenticationException("clientId对应的配置信息不存在" + clientId);
            }

//        String encode = passwordEncoder.encode(clientSecret);
//        if (!StringUtils.equals(clientDetails.getClientSecret(), encode)) {
//            throw new UnapprovedClientAuthenticationException("clientSecret不匹配" + clientId);
//        }

            CustomAuthenticationToken customAuthenticationToken = (CustomAuthenticationToken) authentication;

            TokenRequest tokenRequest = new TokenRequest(Maps.newHashMap(), clientId, clientDetails.getScope(), "");

            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

            CustomLoginDto customLoginDto = (CustomLoginDto)customAuthenticationToken.getData().get("userInfo");

            // 终端渠道
            String channel = request.getHeader(Constants.CHANNEL);

            // 用户当前使用token
            String token;
            // 验证是否已存在token,如果存在则刷新
            String userToken = ObjectUtils.toString(redisUtil.getString(channel + ":" + String.format(Constants.TOKEN_KEY, customLoginDto.getLoginName())));
            LogUtil.info(log,"====【userValidateLogin】用户已存在的token-userToken：" + userToken);
            if (StringUtils.isNotEmpty(userToken)) {
                // 删除原token用户ID
                redisUtil.deleteKey(userToken);
                token = RedisTokenUtil.refreshUserToken(channel, customLoginDto.getLoginName() + "", oAuth2AccessToken.getValue());
            } else {
                //可能返回原来token
                token = RedisTokenUtil.getUserToken(channel, customLoginDto.getLoginName() + "", oAuth2AccessToken.getValue());
            }

            //todo  重写 refresh token接口 ,兼容旧系统
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(oAuth2AccessToken));
            if (!token.equals(oAuth2AccessToken.getValue())) {
                //将jwt生成的token替换成旧系统生成的token
                jsonObject.put("value", token);
            }

            jsonObject.put("userInfo", customLoginDto);

            LogUtil.info(log, "登录成功:{}" + authentication.getName());
            String resp = JSON.toJSONString(MessageRes.success(jsonObject));
            response.getWriter().write(resp);
        }catch (Exception e) {
            LogUtil.error(log,"手机验证码登录异常",e);
            String result = JSON.toJSONString(MessageRes.fail(FailureCodeEnum.GL999999.getCode(),FailureCodeEnum.GL999999.getMsg()));
            response.getWriter().write(result);
        }
    }


    /**
     * 获取头部信息
     * @param header
     * @param request
     * @return
     * @throws IOException
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, "UTF-8");

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

}
