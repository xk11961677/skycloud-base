package com.skycloud.base.authentication.service;

import com.skycloud.base.authentication.model.dto.UserDto;
import com.skycloud.base.authentication.model.vo.UserVo;
import com.sky.framework.model.dto.Pagination;
import com.sky.framework.web.support.IService;
import com.skycloud.base.authentication.model.domain.User;

/**
 * 用户表
 *
 * @author
 * @date 2019-06-04 14:19:39
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户
     * @param userDto
     * @return
     */
    Pagination<UserVo> queryUserByPage(UserDto userDto);
}
