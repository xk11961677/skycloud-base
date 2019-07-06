package com.skycloud.base.authentication.mapper;

import com.skycloud.base.authentication.model.domain.User;
import com.sky.framework.web.mybatis.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户表
 *
 * @author lws
 * @date 2019-06-04 14:19:39
 */
@Mapper
@Repository
public interface UserMapper extends MyMapper<User> {

}
