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
package com.skycloud.base.idgenerator.config;

import com.skycloud.base.idgenerator.baidu.UidGenerator;
import com.skycloud.base.idgenerator.baidu.impl.CachedUidGenerator;
import com.skycloud.base.idgenerator.baidu.impl.DefaultUidGenerator;
import com.skycloud.base.idgenerator.baidu.worker.DisposableWorkerIdAssigner;
import com.skycloud.base.idgenerator.baidu.worker.WorkerIdAssigner;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
@MapperScan("com.skycloud.base.idgenerator.baidu.worker.dao")
public class UidGeneratorConfiguration {

    @Autowired
    private UidGeneratorProperties properties;

    @Bean("disposableWorkerIdAssigner")
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }


    @Bean("cachedUidGenerator")
    public UidGenerator cachedUidGenerator(WorkerIdAssigner disposableWorkerIdAssigner) {
        CachedUidGenerator cachedUidGenerator = new CachedUidGenerator();
        cachedUidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        UidGeneratorProperties.CachedUidGeneratorProperties cache = properties.getCache();
        cachedUidGenerator.setTimeBits(cache.getTimeBits());
        cachedUidGenerator.setWorkerBits(cache.getWorkerBits());
        cachedUidGenerator.setSeqBits(cache.getSeqBits());
        cachedUidGenerator.setEpochStr(cache.getEpochStr());
        if (cache.getBoostPower() > 0) {
            cachedUidGenerator.setBoostPower(cache.getBoostPower());
        }
        if (cache.getPaddingFactor() > 0) {
            cachedUidGenerator.setPaddingFactor(cache.getPaddingFactor());
        }
        if (cache.getScheduleInterval() > 0) {
            cachedUidGenerator.setScheduleInterval(cache.getScheduleInterval());
        }
        return cachedUidGenerator;
    }


    @Bean("defaultUidGenerator")
    public UidGenerator defaultUidGenerator(WorkerIdAssigner disposableWorkerIdAssigner) {
        DefaultUidGenerator defaultUidGenerator = new DefaultUidGenerator();
        defaultUidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        UidGeneratorProperties.DefaultUidGeneratorProperties defaultp = properties.getDefaultp();
        defaultUidGenerator.setTimeBits(defaultp.getTimeBits());
        defaultUidGenerator.setWorkerBits(defaultp.getWorkerBits());
        defaultUidGenerator.setSeqBits(defaultp.getSeqBits());
        defaultUidGenerator.setEpochStr(defaultp.getEpochStr());
        return defaultUidGenerator;
    }


}
