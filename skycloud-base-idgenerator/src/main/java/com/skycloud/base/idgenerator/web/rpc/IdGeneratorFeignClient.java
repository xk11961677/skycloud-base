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
package com.skycloud.base.idgenerator.web.rpc;

import com.sky.framework.model.dto.MessageRes;
import com.skycloud.base.idgenerator.api.service.IdGeneratorFeignApi;
import com.skycloud.base.idgenerator.baidu.UidGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@RestController
@Api(value = "API - IdGeneratorFeignClient", tags = "idGeneratorFeignClient", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class IdGeneratorFeignClient implements IdGeneratorFeignApi {

    @Autowired
    private UidGenerator cachedUidGenerator;

    @ApiOperation(httpMethod = "POST", value = "获取ID")
    @Override
    public MessageRes<Long> getUid() {
        long uid = cachedUidGenerator.getUID();
        return MessageRes.success(uid);
    }

    @Override
    public MessageRes<List<Long>> getUidByBatch(int num) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            long uid = cachedUidGenerator.getUID();
            list.add(uid);
        }
        return MessageRes.success(list);
    }

    @Override
    public MessageRes<Long> parseUid(Long uid) {
        String value = "";
        if (uid != null) {
            value = cachedUidGenerator.parseUID(uid);
        }
        return MessageRes.success(value);
    }
}
