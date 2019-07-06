package com.skycloud.base.authentication.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author
 */
public interface AuthenticationService {
    /**
     * 校验权限
     *
     * @param authRequest
     * @return 是否有权限
     */
    boolean decide(HttpServletRequest authRequest);

}
