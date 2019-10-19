package com.skycloud.base.codegen.service.impl;

import com.skycloud.base.codegen.model.po.Codegen;
import com.skycloud.base.codegen.service.CodegenService;
import com.skycloud.base.codegen.mapper.CodegenMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 代码生成器表
 *
 * @author sky
 * @date 2019-10-19 10:34:13
 */
@Service("codegenService")
public class CodegenServiceImpl extends ServiceImpl<CodegenMapper,Codegen> implements CodegenService {


}
