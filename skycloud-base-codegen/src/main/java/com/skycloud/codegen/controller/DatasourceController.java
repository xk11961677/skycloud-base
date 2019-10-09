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
package com.skycloud.codegen.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.framework.model.dto.MessageRes;
import com.skycloud.codegen.common.MultiRouteDataSource;
import com.skycloud.codegen.model.po.DataSourceEntity;
import com.skycloud.codegen.model.form.BaseForm;
import com.skycloud.codegen.service.DatasourceService;
import com.skycloud.codegen.util.PageUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据源管理
 *
 * @author
 */
@RestController
@RequestMapping("/datasource")
@Slf4j
public class DatasourceController {

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private MultiRouteDataSource multiRouteDataSource;

    /**
     * 分页查询所有数据
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    @ApiOperation("分页查询所有数据")
    @ApiImplicitParams(
            {@ApiImplicitParam(paramType = "query", dataType = "String", name = "current", value = "当前页", defaultValue = "1", required = true),
                    @ApiImplicitParam(paramType = "query", dataType = "String", name = "size", value = "一页大小", defaultValue = "10", required = true),
                    @ApiImplicitParam(paramType = "query", dataType = "Boolean", name = "ifCount", value = "是否查询总数", defaultValue = "true"),
                    @ApiImplicitParam(paramType = "query", dataType = "String", name = "ascs", value = "升序字段，多个用逗号分隔"),
                    @ApiImplicitParam(paramType = "query", dataType = "String", name = "descs", value = "降序字段，多个用逗号分隔"),
                    @ApiImplicitParam(paramType = "query", dataType = "String", name = "pluginName", value = "插件名"),
                    @ApiImplicitParam(paramType = "query", dataType = "String", name = "pluginType", value = "插件类型")
            })
    public MessageRes<IPage<DataSourceEntity>> selectPage() {
        BaseForm<DataSourceEntity> baseForm = new BaseForm();
        IPage page = this.datasourceService.page(baseForm.getPlusPagingQueryEntity(), pageQueryWrapperCustom(baseForm.getParameters()));
        return MessageRes.success(page);
    }

    @GetMapping("/select")
    @ApiOperation("查询所有数据")
    public MessageRes<List<DataSourceEntity>> selectAll() {
        List<DataSourceEntity> list = this.datasourceService.list();
        return MessageRes.success(list);
    }

    /**
     * 自定义查询组装
     *
     * @param map
     * @return
     */
    protected QueryWrapper<DataSourceEntity> pageQueryWrapperCustom(Map<String, Object> map) {
        // mybatis plus 分页相关的参数
        Map<String, Object> pageHelperParams = PageUtils.filterPageParams(map);
        log.info("分页相关的参数: {}", pageHelperParams);
        //过滤空值，分页查询相关的参数
        Map<String, Object> columnQueryMap = PageUtils.filterColumnQueryParams(map);
        log.info("字段查询条件参数为: {}", columnQueryMap);

        QueryWrapper<DataSourceEntity> queryWrapper = new QueryWrapper<>();

        //排序 操作
        pageHelperParams.forEach((k, v) -> {
            switch (k) {
                case "ascs":
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(StrUtil.toString(v)));
                    break;
                case "descs":
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(StrUtil.toString(v)));
                    break;
            }
        });

        //遍历进行字段查询条件组装
        columnQueryMap.forEach((k, v) -> {
            switch (k) {
                case "pluginName":
                    queryWrapper.like(StrUtil.toUnderlineCase(k), v);
                    break;
                default:
                    queryWrapper.eq(StrUtil.toUnderlineCase(k), v);
            }
        });

        return queryWrapper;
    }


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation("通过主键查询单条数据")
    @GetMapping("/detail")
    public MessageRes<DataSourceEntity> selectOne(@RequestParam Serializable id) {
        return MessageRes.success(this.datasourceService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param dataSourceEntity 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增数据")
    @PostMapping("/add")
    public MessageRes<Boolean> insert(@RequestBody DataSourceEntity dataSourceEntity) {
        boolean save = this.datasourceService.save(dataSourceEntity);
        multiRouteDataSource.dynamicAddDatasource(dataSourceEntity);
        return MessageRes.success(save);
    }

    /**
     * 修改数据
     *
     * @param dataSourceEntity 实体对象
     * @return 修改结果
     */
    @PostMapping("/update")
    @ApiOperation("修改数据")
    public MessageRes<Boolean> update(@RequestBody DataSourceEntity dataSourceEntity) {
        boolean b = this.datasourceService.updateById(dataSourceEntity);
        multiRouteDataSource.dynamicUpdateDatasource(dataSourceEntity);
        return MessageRes.success(b);
    }

    /**
     * 删除数据
     *
     * @param ids 主键结合
     * @return 删除结果
     */
    @PostMapping("/remove")
    @ApiOperation("删除数据")
    public MessageRes<Boolean> delete(@RequestParam("ids") List<Long> ids) {
        List<DataSourceEntity> dataSourceEntities = (List) this.datasourceService.listByIds(ids);
        boolean b = this.datasourceService.removeByIds(ids);
        multiRouteDataSource.dynamicRemoveDatasource(dataSourceEntities);
        return MessageRes.success(b);
    }

    /**
     * 拷贝数据
     *
     * @param id 主键结合
     * @return 拷贝结果
     */
    @PostMapping("/copy")
    @ApiOperation("拷贝数据")
    public MessageRes<Boolean> copy(@RequestParam("id") Long id) {
        DataSourceEntity dataSourceEntity = this.datasourceService.getById(id);
        dataSourceEntity.setId(null);
        boolean save = this.datasourceService.save(dataSourceEntity);
        return MessageRes.success(save);
    }

}
