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
package com.skycloud.base.log.manager.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import com.sky.framework.common.LogUtils;
import com.skycloud.base.log.manager.disruptor.event.LogEvent;
import com.skycloud.base.log.mapper.SystemLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Slf4j
@Component
public class LogEventHandler implements EventHandler<LogEvent> {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public void onEvent(LogEvent logEvent, long sequence, boolean endOfBatch) throws Exception {
        try {
            systemLogMapper.insertSelective(logEvent.getSystemLog());
        } catch (Exception e) {
            LogUtils.error(log, "disruptor LogEventHandler exception:{}", e);
        } finally {
            logEvent.clear();
        }
    }
}
