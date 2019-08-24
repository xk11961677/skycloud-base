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
package com.skycloud.base.upload.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 阿里云签名后返回信息
 *
 * @author
 */
@Data
public class AliyunOssTokenPolicyVo {

    @ApiModelProperty("访问标识")
    private String accessKeyId;

    @ApiModelProperty("访问政策")
    private String policy;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("地址")
    private String host;

    @ApiModelProperty("上传目录")
    private String dir;

    @ApiModelProperty("过期时间")
    private String expire;

    @ApiModelProperty("回调地址")
    private String callback;
}
