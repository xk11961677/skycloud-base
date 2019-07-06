package com.skycloud.base.authentication.mapper;

import com.skycloud.base.authentication.model.domain.Resource;
import com.sky.framework.web.mybatis.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author
 */
@Mapper
@Repository
public interface ResourceMapper extends MyMapper<Resource> {

    @Select("SELECT id,code,type,name,url,method,description,created_time,updated_time,created_by,updated_by" +
            " FROM dl_resources")
    Set<Resource> findAll();

    @Select("<script>" +
            "SELECT DISTINCT rs.code,rs.url,rs.name,rs.type,rs.method,rs.description" +
            " FROM dl_role r" +
            " INNER JOIN dl_role_resources_relation rrr ON r.id = rrr.role_id" +
            " INNER JOIN dl_resources rs ON rs.id = rrr.resource_id" +
            " WHERE r.code IN " +
            " <fosky collection='roleCodes' item='roleCode' index='index' open='(' close=')' separator=',' >" +
            "    #{roleCode}" +
            " </fosky>" +
            "</script>")
    Set<Resource> queryByRoleCodes(@Param("roleCodes") String[] roleCodes);
}