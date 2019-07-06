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
