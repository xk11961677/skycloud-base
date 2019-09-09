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
package com.skycloud.codegen.config.datasource;

import cn.hutool.core.bean.BeanUtil;
import com.skycloud.codegen.common.MultiRouteDataSource;
import com.skycloud.codegen.entity.DataSourceEntity;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author
 */
@Configuration
public class DataSourceConfig {

    public static ArrayList<DataSourceEntity> dataSourcesContainer = new ArrayList<>();


    @Autowired
    private OneDataSourceProperties oneDataSourceProperties;

    @Autowired
    private TwoDataSourceProperties twoDataSourceProperties;

    @Autowired
    private ThreeDataSourceProperties threeDataSourceProperties;

    @Bean(name = "oneDataSource")
    public HikariDataSource oneDataSource() {
        HikariDataSource oneDataSource = new HikariDataSource();
        oneDataSource.setJdbcUrl(oneDataSourceProperties.getUrl());
        oneDataSource.setUsername(oneDataSourceProperties.getUser());
        oneDataSource.setPassword(oneDataSourceProperties.getPassword());
        DataSourceEntity entity = new DataSourceEntity();
        BeanUtil.copyProperties(oneDataSourceProperties, entity);
        entity.setName("one");
        dataSourcesContainer.add(entity);
        return oneDataSource;
    }

    @Bean(name = "twoDataSource")
    public HikariDataSource twoDataSource() {
        HikariDataSource twoDataSource = new HikariDataSource();
        twoDataSource.setJdbcUrl(twoDataSourceProperties.getUrl());
        twoDataSource.setUsername(twoDataSourceProperties.getUser());
        twoDataSource.setPassword(twoDataSourceProperties.getPassword());
        DataSourceEntity entity = new DataSourceEntity();
        BeanUtil.copyProperties(twoDataSourceProperties, entity);
        entity.setName("two");
        dataSourcesContainer.add(entity);
        return twoDataSource;
    }

    @Bean(name = "threeDataSource")
    public HikariDataSource threeDataSource() {
        HikariDataSource threeDataSource = new HikariDataSource();
        threeDataSource.setJdbcUrl(threeDataSourceProperties.getUrl());
        threeDataSource.setUsername(threeDataSourceProperties.getUser());
        threeDataSource.setPassword(threeDataSourceProperties.getPassword());
        DataSourceEntity entity = new DataSourceEntity();
        BeanUtil.copyProperties(threeDataSourceProperties, entity);
        entity.setName("three");
        dataSourcesContainer.add(entity);
        return threeDataSource;
    }


    @Primary
    @Bean(name = "multiDataSource")
    public MultiRouteDataSource multiRouteDataSource() {
        MultiRouteDataSource multiDataSource = new MultiRouteDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("one", oneDataSource());
        targetDataSources.put("two", twoDataSource());
        targetDataSources.put("three", threeDataSource());
        multiDataSource.setTargetDataSources(targetDataSources);
        multiDataSource.setDefaultTargetDataSource(oneDataSource());
        return multiDataSource;
    }


    /**
     *
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("multiDataSource") DataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dynamicDataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml");
        bean.setMapperLocations(resources);
        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     *
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(multiRouteDataSource());
    }
}
