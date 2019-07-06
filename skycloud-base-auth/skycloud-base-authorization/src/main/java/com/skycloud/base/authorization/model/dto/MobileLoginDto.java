package com.skycloud.base.authorization.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 手机号验证码登录
 * @author
 */
@Data
public class MobileLoginDto extends CustomLoginDto {

    @ApiModelProperty(value = "手机号码",required = true)
    @NotEmpty(message = "手机号码不能为空!")
    private String mobile;

    @ApiModelProperty(value = "验证码",required = true)
    @NotEmpty(message = "验证码不能为空!")
    private String code;

}
