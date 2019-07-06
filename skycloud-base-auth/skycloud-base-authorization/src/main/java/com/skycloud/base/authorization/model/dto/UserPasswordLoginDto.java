package com.skycloud.base.authorization.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author
 */
@Data
@ApiModel("UserPasswordLoginDto")
public class UserPasswordLoginDto extends CustomLoginDto {

    @ApiModelProperty(value = "用户名不能为空",required = true)
    @NotEmpty(message = "用户名不能为空!")
    private String username;

    @ApiModelProperty(value = "密码不能为空",required = true)
    @NotEmpty(message = "密码不能为空!")
    private String password;
}
