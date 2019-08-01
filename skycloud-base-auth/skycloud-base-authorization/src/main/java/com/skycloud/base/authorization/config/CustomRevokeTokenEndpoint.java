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
package com.skycloud.base.authorization.config;

import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.redis.util.RedisUtils;
import com.skycloud.base.authorization.common.Constants;
import com.skycloud.base.authorization.exception.AuzBussinessException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author
 */
@Api(value = "WEB - CustomRevokeTokenEndpoint", tags = "logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Slf4j
public class CustomRevokeTokenEndpoint {

    @Autowired
    @Qualifier("consumerTokenServices")
    private ConsumerTokenServices consumerTokenServices;


    /**
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/oauth/logout", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "退出登录", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "channel", value = "channel", required = true, paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "deviceNo", value = "deviceNo", paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "deviceId", value = "deviceId", paramType = "header", dataType = "String"),
            @ApiImplicitParam(name = "appSource", value = "appSource", paramType = "header", dataType = "String")
    })
    @ApiParam
    @ResponseBody
    public MessageRes revokeToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String channel = request.getHeader(Constants.CHANNEL);
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(channel)) {
            throw new AuzBussinessException(FailureCodeEnum.GL990001.getCode(), FailureCodeEnum.GL990001.getMsg());
        }
        if (!token.contains("bearer ")) {
            token = token.substring(7);
        }

        String userId = ObjectUtils.toString(RedisUtils.getString(token));

        RedisUtils.deleteKey(String.format(channel + ":" + Constants.TOKEN_KEY, userId));
        RedisUtils.deleteKey(token);

        return MessageRes.success(true);
    }

}
