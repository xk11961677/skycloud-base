package com.skycloud.base.authentication.model.domain;

import com.sky.framework.web.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Table;


/**
 * 用户表
 *
 * @author
 * @date 2019-06-04 14:19:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "dl_user" )
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码密文
     */
    private String password;
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
    @Column(name = "account_non_expired" )
    private Boolean accountNonExpired;
    /**
     * 密码是否未过期
     */
    @Column(name = "credentials_non_expired" )
    private Boolean credentialsNonExpired;
    /**
     * 是否未锁定
     */
    @Column(name = "account_non_locked" )
    private Boolean accountNonLocked;

}
