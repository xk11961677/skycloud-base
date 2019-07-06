package com.skycloud.base.authorization.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author
 */
@Data
public class CustomLoginDto {

    @ApiModelProperty(value = "渠道", hidden = true)
    @NotEmpty(message = "渠道不能为空!")
    private String channel;


    /**
     * 手机号或用户名
     */
    @ApiModelProperty(value = "手机号或用户名", hidden = true)
    private String loginName;

    /**
     *
     */
    @ApiModelProperty(value = "密码", hidden = true)
    private String password;

    /**
     *
     */
    @ApiModelProperty(value = "登录IP", hidden = true)
    private String loginIp;

    @ApiModelProperty(value = "设备号", hidden = true)
    private String deviceId;

    @ApiModelProperty(value = "来源", hidden = true)
    private String appChannel;

}
