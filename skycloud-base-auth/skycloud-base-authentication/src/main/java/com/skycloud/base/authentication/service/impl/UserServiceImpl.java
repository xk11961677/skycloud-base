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

import com.sky.framework.mybatis.service.BaseService;
import com.skycloud.base.authentication.api.exception.AucBussinessException;
import com.skycloud.base.authentication.api.model.dto.UserLoginDTO;
import com.skycloud.base.authentication.api.model.vo.RoleVO;
import com.skycloud.base.authentication.api.model.vo.UserLoginVO;
import com.skycloud.base.authentication.mapper.RoleMapper;
import com.skycloud.base.authentication.mapper.UserMapper;
import com.skycloud.base.authentication.model.po.Role;
import com.skycloud.base.authentication.model.po.User;
import com.skycloud.base.authentication.service.UserService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户表
 *
 * @author code generator
 * @date 2019-09-11 13:34:28
 */
@Service("userService")
public class UserServiceImpl extends BaseService<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDto) {
        User userQuery = new User();
        userQuery.setUsername(userLoginDto.getUsername());
        userQuery.setDisabled(0);
        User user = userMapper.selectOne(userQuery);
        if (user == null) {
            throw new AucBussinessException();
        }
        userQuery.setId(user.getId());
        List<Role> roles = roleMapper.listByUser(userQuery);
        UserLoginVO userLoginVo = mapperFacade.map(user, UserLoginVO.class);
        List<RoleVO> roleVos = mapperFacade.mapAsList(roles, RoleVO.class);
        userLoginVo.setRoles(roleVos);
        return userLoginVo;
    }

}
