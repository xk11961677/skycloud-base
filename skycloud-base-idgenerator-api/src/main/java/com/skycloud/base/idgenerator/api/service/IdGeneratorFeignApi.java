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
package com.skycloud.base.idgenerator.api.service;

import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.common.feign.CustomFeignAutoConfiguration;
import com.skycloud.base.idgenerator.api.service.factory.IdGeneratorFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * The interface order feign api.
 *
 * @author
 */
@FeignClient(name = "skycloud-base-idgenerator-api", configuration = CustomFeignAutoConfiguration.class, fallbackFactory = IdGeneratorFeignFallbackFactory.class)
public interface IdGeneratorFeignApi {

    /**
     * 获取单个ID
     *
     * @return
     */
    @PostMapping(value = "/api/idgenerator/getUid")
    MessageRes<Long> getUid();

    /**
     * 批量获取ID
     *
     * @param num
     * @return
     */
    @PostMapping(value = "/api/idgenerator/getUidByBatch")
    MessageRes<List<Long>> getUidByBatch(int num);

    /**
     * 解析ID
     *
     * @param uid
     * @return
     */
    @PostMapping(value = "/api/idgenerator/parseUid")
    MessageRes<Long> parseUid(Long uid);

}
