package com.skycloud.base.authentication.api.service;

import com.sky.framework.model.dto.MessageRes;
import org.springframework.security.jwt.Jwt;

/**
 * @author
 */
public interface AuthService {
    /**
     * 调用签权服务，判断用户是否有权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return Result
     */
    MessageRes authenticate(String authentication, String url, String method);

    /**
     * 判断url是否在忽略的范围内
     * 只要是配置中的开头，即返回true
     *
     * @param url
     * @return
     */
    boolean ignoreAuthentication(String url);

    /**
     * 查看签权服务器返回结果，有权限返回true
     *
     * @param authResult
     * @return
     */
    boolean hasPermission(MessageRes authResult);

    /**
     * 是否无效authentication
     *
     * @param authentication
     * @return
     */
    boolean invalidJwtAccessToken(String authentication);

    /**
     * 调用签权服务，判断用户是否有权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return true/false
     */
    boolean hasPermission(String authentication, String url, String method);

    /**
     * 从认证信息中提取jwt token 对象
     *
     * @param authentication 认证信息  Authorization: bearer header.payload.signature
     * @return Jwt对象
     */
    Jwt getJwt(String authentication);

    /**
     * 验证token是否超时
     * @param authentication
     * @return
     */
    boolean checkJwtAccessToken(String authentication);


    /**
     * 验证token是否超时
     * @param authentication
     * @return
     */
    String checkJwtRedis(String authentication);

    /**
     * 兼容旧token
     * @param authentication
     * @return
     */
    String getJwtOrNoOld(String authentication);
}
