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
package com.skycloud.base.upload.web.frontend;

import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.upload.model.vo.AliyunOssCallbackVo;
import com.skycloud.base.upload.model.vo.AliyunOssTokenPolicyVo;
import com.skycloud.base.upload.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;


/**
 * 上传信息
 *
 * @author
 */
@RestController
@AllArgsConstructor
@RequestMapping("/open/oss")
@Api(value = "WEB - OssController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Slf4j
public class OssController {

    @Resource
    private OssService ossService;

    @ApiOperation(value = "oss上传签名生成")
    @RequestMapping(value = "/policy")
    public MessageRes<AliyunOssTokenPolicyVo> policy() {
        AliyunOssTokenPolicyVo policy = ossService.policy();
        return MessageRes.success(policy);
    }

    @ApiOperation(value = "oss上传成功回调")
    @PostMapping(value = "callback")
    public MessageRes<AliyunOssCallbackVo> callback(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            Arrays.stream(entry.getValue()).forEach(t ->
                    LogUtils.info(log, "key:{} value:{}", entry.getKey(), t)
            );
        }
        AliyunOssCallbackVo ossCallbackResult = ossService.callback(request);
        return MessageRes.success(ossCallbackResult);
    }
}
