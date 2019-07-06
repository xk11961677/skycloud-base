package com.skycloud.base.authentication.service;

import com.skycloud.base.authentication.model.domain.Resource;

import java.util.Set;

/**
 * @author
 */
public interface ResourceService {
    /**
     * 返回所有的资源定义内容，resources表中
     *
     * @return
     */
    Set<Resource> findAll();

    /**
     * 根据角色code查询到角色把对应的资源定义
     *
     * @param roleCodes
     * @return
     */
    Set<Resource> queryByRoleCodes(String[] roleCodes);
}
