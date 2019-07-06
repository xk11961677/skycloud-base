package com.skycloud.base.authorization.swagger;

import com.skycloud.base.authorization.model.dto.MobileLoginDto;
import com.skycloud.base.authorization.model.dto.UserPasswordLoginDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 */
@RestController
@Api(value = "WEB - SwaggerModelDto", tags = "接口无用",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SwaggerModelDto {

    @ApiOperation(value = "test")
    @PostMapping("/test")
    public void test(@RequestBody UserPasswordLoginDto userPasswordLoginDto) {

    }


    @ApiOperation(value = "test2")
    @PostMapping("/test2")
    public void test2(@RequestBody MobileLoginDto mobileLoginDto) {

    }
}
