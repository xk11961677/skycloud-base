package com.skycloud.base.authentication.web;

import com.skycloud.base.authentication.model.domain.User;
import com.skycloud.base.authentication.model.dto.UserDto;
import com.skycloud.base.authentication.model.vo.UserVo;
import com.skycloud.base.authentication.service.UserService;
import com.sky.framework.model.dto.MessageReq;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.dto.Pagination;
import com.sky.framework.model.util.UserContextHolder;
import com.sky.framework.web.support.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;


/**
 * 用户表
 *
 * @author
 * @date 2019-06-04 14:19:39
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user" )
@Api(value = "WEB - UserController" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@SuppressWarnings("all" )
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private MapperFacade mapperFacade;

    /**
     * @param messageReq
     * @return
     */
    @PostMapping("addUser" )
    @ApiOperation(httpMethod = "POST" , value = "增加用户" )
    public MessageRes<Boolean> addUser(@RequestBody UserDto userDto) {
        User user = mapperFacade.map(userDto, User.class);
        user.setCreatedBy(UserContextHolder.getInstance().getUsername());
        user.setCreatedTime(new Date());
        int result = userService.save(user);
        return MessageRes.success(result > 0 ? true : false);
    }


    /**
     * @param messageReq
     * @return
     */
    @PostMapping("removeUser" )
    @ApiOperation(httpMethod = "POST" , value = "删除用户" )
    public MessageRes<Boolean> removeUser(@Valid @RequestBody MessageReq<UserDto> messageReq) {
        UserDto userDto = messageReq.getParam();
        User user = mapperFacade.map(userDto, User.class);
        user.setDisabled(1);

        user.setUpdatedBy(UserContextHolder.getInstance().getUsername());
        user.setUpdatedTime(new Date());
        int result = userService.update(user);
        return MessageRes.success(result > 0 ? true : false);
    }

    /**
     * @param messageReq
     * @return
     */
    @PostMapping("modifyUser" )
    @ApiOperation(httpMethod = "POST" , value = "修改用户" )
    public MessageRes<Boolean> modifyUser(@Valid @RequestBody MessageReq<UserDto> messageReq) {
        UserDto userDto = messageReq.getParam();
        User user = mapperFacade.map(userDto, User.class);

        user.setUpdatedBy(UserContextHolder.getInstance().getUsername());
        user.setUpdatedTime(new Date());

        int result = userService.update(user);
        return MessageRes.success(result > 0 ? true : false);
    }


    /**
     * @param
     * @return
     */
    @PostMapping("queryUserByPage" )
    @ApiOperation(httpMethod = "POST" , value = "分页查询用户" )
    public MessageRes<Pagination<UserVo>> queryUserByPage(@Valid @RequestBody MessageReq<UserDto> messageReq) {
        UserDto userDto = messageReq.getParam();
        Pagination<UserVo> page = userService.queryUserByPage(userDto);
        return MessageRes.success(page);
    }
}
