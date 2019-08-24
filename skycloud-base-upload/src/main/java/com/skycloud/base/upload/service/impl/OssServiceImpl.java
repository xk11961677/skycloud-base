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
package com.skycloud.base.upload.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.framework.common.DateUtils;
import com.sky.framework.oss.model.UploadToken;
import com.sky.framework.oss.property.OssProperties;
import com.sky.framework.oss.util.OssUtils;
import com.skycloud.base.upload.model.vo.AliyunOssCallbackVo;
import com.skycloud.base.upload.model.vo.AliyunOssTokenPolicyVo;
import com.skycloud.base.upload.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 上传service实现类
 *
 * @author
 */
@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private OssProperties ossProperties;

    @Override
    public AliyunOssTokenPolicyVo policy() {
        UploadToken uploadToken = new UploadToken();
        uploadToken.setUploadDir(ossProperties.getDirPrefix() + DateUtils.getYear() + "/" + DateUtils.getMonth() + "/" + DateUtils.getDay());
        Map<String, Object> policy = OssUtils.createUploadToken(uploadToken);
        AliyunOssTokenPolicyVo vo = BeanUtil.mapToBean(policy, AliyunOssTokenPolicyVo.class, true);
        return vo;
    }

    @Override
    public AliyunOssCallbackVo callback(HttpServletRequest request) {
        AliyunOssCallbackVo result = new AliyunOssCallbackVo();
        String filename = request.getParameter("filename");
        filename = ossProperties.getUrlPrefix().concat("/").concat(filename);
        result.setFilename(filename);
        result.setSize(request.getParameter("size"));
        result.setMimeType(request.getParameter("mimeType"));
        result.setWidth(request.getParameter("width"));
        result.setHeight(request.getParameter("height"));
        return result;
    }
}
