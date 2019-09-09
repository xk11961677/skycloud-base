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

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skycloud.codegen.common.DataSourceContext;
import com.skycloud.codegen.config.datasource.DataSourceConfig;
import com.skycloud.codegen.entity.DataSourceEntity;
import com.skycloud.codegen.entity.GenConfig;
import com.skycloud.codegen.service.SysGeneratorService;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.dto.Pagination;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author
 */
@RestController
@AllArgsConstructor
@RequestMapping("/generator")
public class SysGeneratorController {
	private final SysGeneratorService sysGeneratorService;

	@GetMapping("/datasource")
	public MessageRes<Pagination> datasource() {
		Pagination<DataSourceEntity> pagination = new Pagination(Pagination.DEFAULT_PAGE_SIZE, 1,DataSourceConfig.dataSourcesContainer.size(), DataSourceConfig.dataSourcesContainer);
		return MessageRes.success(pagination);
	}

	/**
	 * 列表
	 *
	 * @param tableName 参数集
	 * @return 数据库表
	 */
	@GetMapping("/page")
	public MessageRes<Pagination> list(Page page, String tableName, @RequestParam(required = false) String datasource) {
		if (!StringUtils.isEmpty(datasource)) {
			DataSourceContext.setDataSource(datasource);
		}
		IPage<List<Map<String, Object>>> listIPage = sysGeneratorService.queryPage(page, tableName);
		DataSourceContext.clearDataSource();
		int pageSize = Integer.parseInt(listIPage.getSize() + "");
		int pageNum = Integer.parseInt(listIPage.getCurrent() + "");
		int totalCount = Integer.parseInt(listIPage.getTotal() + "");
		List records = listIPage.getRecords();
		Pagination<DataSourceEntity> pagination = new Pagination(pageSize, pageNum, totalCount, records);
		return MessageRes.success(pagination);
	}

	/**
	 * 生成代码
	 */
	@PostMapping("/code")
	public void code(@RequestBody GenConfig genConfig, HttpServletResponse response) throws IOException {
		String datasource = genConfig.getDatasource();
		if (!StringUtils.isEmpty(datasource)) {
			DataSourceContext.setDataSource(datasource);
		}
		byte[] data = sysGeneratorService.generatorCode(genConfig);
		DataSourceContext.clearDataSource();

		response.reset();
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s.zip", genConfig.getTableName()));
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream; charset=UTF-8");

		IoUtil.write(response.getOutputStream(), Boolean.FALSE, data);
	}
}
