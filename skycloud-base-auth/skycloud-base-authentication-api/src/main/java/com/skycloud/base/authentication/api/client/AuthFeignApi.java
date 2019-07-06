package com.skycloud.base.authentication.api.client;

import com.skycloud.base.authentication.api.client.hystrix.AuthFeignHystrix;
import com.sky.framework.model.dto.MessageRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author
 */
@Component
@FeignClient(name = "skycloud-base-authentication", fallback = AuthFeignHystrix.class)
public interface AuthFeignApi {
    /**
     * 调用签权服务，判断用户是否有权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return
     */
    @PostMapping(value = "/auth/permission")
    @ResponseBody
    MessageRes auth(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication, @RequestParam("url") String url, @RequestParam("method") String method);

}
