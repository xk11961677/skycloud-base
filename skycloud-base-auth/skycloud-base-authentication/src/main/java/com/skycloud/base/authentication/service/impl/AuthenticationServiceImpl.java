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
package com.skycloud.base.authentication.service.impl;

import com.sky.framework.common.LogUtils;
import com.skycloud.base.authentication.common.ResourceMatcherContainer;
import com.skycloud.base.authentication.enums.ResourceTypeEnum;
import com.skycloud.base.authentication.model.po.Resource;
import com.skycloud.base.authentication.service.AuthenticationService;
import com.skycloud.base.authentication.common.CustomMvcRequestMatcher;
import com.skycloud.base.authentication.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private HandlerMappingIntrospector mvcHandlerMappingIntrospector;

    /**
     * 未在资源库中的URL默认标识
     */
    public static final String NONEXISTENT_URL = "NONEXISTENT_URL";

    @Autowired
    private ResourceService resourceService;

    @Override
    public void loadResource() {
        Integer[] types = {ResourceTypeEnum.INTERFACE_URL.getKey()};
        List<Resource> resources = resourceService.listApiURL(null, types);
        resources.stream().forEach(r -> addResourceConfigAttributes(r));
        LogUtils.debug(log, "resourceConfigAttributes:{}", () -> ResourceMatcherContainer.getInstance());
    }

    @Override
    public void addResourceConfigAttributes(Resource resource) {
        CustomMvcRequestMatcher matcher = new CustomMvcRequestMatcher(mvcHandlerMappingIntrospector, resource.getUrl(), resource.getMethod());
        ResourceMatcherContainer.getInstance().put(matcher, new SecurityConfig(resource.getCode()));
    }

    @Override
    public void removeResourceConfigAttributes(Resource resource) {
        CustomMvcRequestMatcher matcher = new CustomMvcRequestMatcher(mvcHandlerMappingIntrospector, resource.getUrl(), resource.getMethod());
        ResourceMatcherContainer.getInstance().remove(matcher);
    }

    @Override
    public void updateResourceConfigAttributes(Resource resource) {
        removeResourceConfigAttributes(resource);
        addResourceConfigAttributes(resource);
    }


    /**
     * @param authRequest 访问的url,method
     * @return 有权限true, 无权限或全局资源中未找到请求url返回否
     */
    @Override
    public boolean decide(HttpServletRequest authRequest) {
        log.debug("正在访问的url是:{}，method:{}", authRequest.getServletPath(), authRequest.getMethod());
        //获取用户认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取此url，method访问对应的权限资源信息
        ConfigAttribute urlConfigAttribute = findConfigAttributesByUrl(authRequest);
        if (NONEXISTENT_URL.equals(urlConfigAttribute.getAttribute())) {
            log.debug("url未在资源池中找到，拒绝访问");
        }
        //获取此访问用户所有角色拥有的权限资源
        Set<Resource> userResources = findResourcesByAuthorityRoles(authentication.getAuthorities());
        //用户拥有权限资源 与 url要求的资源进行对比
        return isMatch(urlConfigAttribute, userResources);
    }

    /**
     * url对应资源与用户拥有资源进行匹配
     *
     * @param urlConfigAttribute
     * @param userResources
     * @return
     */
    public boolean isMatch(ConfigAttribute urlConfigAttribute, Set<Resource> userResources) {
        return userResources.stream().anyMatch(resource -> resource.getCode().equals(urlConfigAttribute.getAttribute()));
    }

    /**
     * 根据url和method查询到对应的权限信息
     *
     * @param authRequest
     * @return
     */
    public ConfigAttribute findConfigAttributesByUrl(HttpServletRequest authRequest) {
        return ResourceMatcherContainer.getInstance().keySet().stream()
                .filter(requestMatcher -> requestMatcher.matches(authRequest))
                .map(requestMatcher -> ResourceMatcherContainer.getInstance().get(requestMatcher))
                .peek(urlConfigAttribute -> log.debug("url在资源池中配置：{}", urlConfigAttribute.getAttribute()))
                .findFirst()
                .orElse(new SecurityConfig(NONEXISTENT_URL));
    }

    /**
     * 根据用户所被授予的角色，查询到用户所拥有的资源
     *
     * @param authorityRoles
     * @return
     */
    private Set<Resource> findResourcesByAuthorityRoles(Collection<? extends GrantedAuthority> authorityRoles) {
        //用户被授予的角色
        log.debug("用户的授权角色集合信息为:{}", authorityRoles);
        String[] authorityRoleCodes = authorityRoles.stream()
                .map(GrantedAuthority::getAuthority)
                .map(code -> code.replace("ROLE_", ""))
                .collect(Collectors.toList())
                .toArray(new String[authorityRoles.size()]);
        Set<Resource> resources = new HashSet<>();
        if (authorityRoleCodes.length != 0) {
            Integer[] types = {ResourceTypeEnum.INTERFACE_URL.getKey()};
            resources = resourceService.listByRoleCodes(authorityRoleCodes, types);
        }
        Set<Resource> finalResources = resources;
        LogUtils.debug(log, "用户被授予角色的资源数量是:{}, 资源集合信息为:{}", (Supplier) () -> new Object[]{finalResources.size(), finalResources});
        return resources;
    }
}
