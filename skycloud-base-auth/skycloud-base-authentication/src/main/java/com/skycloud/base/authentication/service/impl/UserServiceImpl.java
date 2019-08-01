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

import com.skycloud.base.authentication.model.vo.UserVo;
import com.skycloud.base.authentication.service.UserService;
import com.skycloud.base.authentication.model.domain.User;
import com.skycloud.base.authentication.model.dto.UserDto;
import com.github.pagehelper.PageHelper;
import com.sky.framework.model.dto.Pagination;
import com.sky.framework.web.support.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户表
 *
 * @author
 * @date 2019-06-04 14:19:39
 */
@Service("userService")
public class UserServiceImpl extends BaseService<User> implements UserService {

    @Resource
    private MapperFacade mapperFacade;

    @Override
    public Pagination<UserVo> queryUserByPage(UserDto userDto) {
        User user = mapperFacade.map(userDto, User.class);
        PageHelper.startPage(user.getPageNum(), user.getPageSize());
        List<User> result = this.select(user);
        List<UserVo> userVos = null;
        if(!CollectionUtils.isEmpty(result)) {
            userVos = mapperFacade.mapAsList(result, UserVo.class);
        }
        Pagination<UserVo> page = new Pagination(user.getPageNum(), user.getPageSize(), 0, userVos);
        return page;
    }
}
