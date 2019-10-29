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
package com.skycloud.base.idgenerator.api.service.fallback;

import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.common.fallback.FallbackResult;
import com.skycloud.base.idgenerator.api.service.IdGeneratorFeignApi;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author
 */
//@Component
@Slf4j
public class IdGeneratorFeignFallback implements IdGeneratorFeignApi {

    @Setter
    private Throwable cause;


    @Override
    public MessageRes<Long> getUid() {
        LogUtils.error(log, "feign fallback :{} ", cause);
        return FallbackResult.fail(cause);
    }

    @Override
    public MessageRes<List<Long>> getUidByBatch(int num) {
        LogUtils.error(log, "feign fallback :{} ", cause);
        return FallbackResult.fail(cause);
    }

    @Override
    public MessageRes<Long> parseUid(Long uid) {
        LogUtils.error(log, "feign fallback :{} ", cause);
        return FallbackResult.fail(cause);
    }

}
