package com.skycloud.base.authentication.model.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户表
 *
 * @author
 * @date 2019-06-04 14:19:39
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户手机
     */
    private String mobile;
    /**
     * 是否有效用户
     */
    private Boolean enabled;
    /**
     * 账号是否未过期
     */
    private Boolean accountNonExpired;
    /**
     * 密码是否未过期
     */
    private Boolean credentialsNonExpired;
    /**
     * 是否未锁定
     */
    private Boolean accountNonLocked;

}
