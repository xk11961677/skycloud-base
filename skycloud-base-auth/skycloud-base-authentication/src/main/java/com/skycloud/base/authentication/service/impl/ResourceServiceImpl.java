package com.skycloud.base.authentication.service.impl;

import com.skycloud.base.authentication.service.ResourceService;
import com.skycloud.base.authentication.mapper.ResourceMapper;
import com.skycloud.base.authentication.model.domain.Resource;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * @author 
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Set<Resource> findAll() {
        return resourceMapper.findAll();
    }

    @Override
    public Set<Resource> queryByRoleCodes(String[] roleCodes) {
        if (ArrayUtils.isNotEmpty(roleCodes)) {
            return resourceMapper.queryByRoleCodes(roleCodes);
        }
        return Collections.emptySet();
    }
}
