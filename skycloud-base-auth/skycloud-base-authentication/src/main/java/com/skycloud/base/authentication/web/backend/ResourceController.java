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
package com.skycloud.base.authentication.web.backend;

import com.skycloud.base.authentication.model.domain.Resource;
import com.skycloud.base.authentication.model.dto.UserDto;
import com.skycloud.base.authentication.service.ResourceService;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.web.support.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 资源表
 *
 * @author code generator
 * @date 2019-09-11 13:33:46
 */
@RestController
@AllArgsConstructor
@RequestMapping("/resources")
@Api(value = "WEB - ResourcesController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Slf4j
public class ResourceController extends BaseController {

    @Autowired
    private ResourceService resourcesService;

    @ApiOperation(httpMethod = "POST", value = "获取菜单")
    @RequestMapping(method = RequestMethod.POST, value = "/getMenu")
    @ResponseBody
    public MessageRes<String> getMenu() {
        UserDto userDto = new UserDto();
        userDto.setId(101L);
        String data = resourcesService.listMenuByUserId(userDto);
        return MessageRes.success(data);
    }

    @ApiOperation(httpMethod = "POST", value = "获取按钮")
    @RequestMapping(method = RequestMethod.POST, value = "/getButton")
    @ResponseBody
    public MessageRes<Resource> getButton() {
        UserDto userDto = new UserDto();
        userDto.setId(101L);
        List<Resource> resources = resourcesService.listButtonByUserId(userDto);
        return MessageRes.success(resources);
    }
}
