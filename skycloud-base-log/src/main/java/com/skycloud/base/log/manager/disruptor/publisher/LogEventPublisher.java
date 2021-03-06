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
package com.skycloud.base.log.manager.disruptor.publisher;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sky.framework.common.LogUtils;
import com.skycloud.base.log.api.model.dto.SystemOptLogDTO;
import com.skycloud.base.log.manager.disruptor.event.LogEvent;
import com.skycloud.base.log.manager.disruptor.factory.LogEventFactory;
import com.skycloud.base.log.manager.disruptor.handler.LogEventHandler;
import com.skycloud.base.log.manager.disruptor.translator.LogEventTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author
 */
@Slf4j
@Component
public class LogEventPublisher implements InitializingBean, DisposableBean {

    private Disruptor<LogEvent> disruptor;

    /**
     * 环形缓冲区大小, 需要2的幂次方
     */
    private int bufferSize = 1024;

    private AtomicBoolean started = new AtomicBoolean(false);

    @Autowired
    private LogEventHandler logEventHandler;


    public void start(int bufferSize) {
        disruptor = new Disruptor<>(new LogEventFactory(),
                bufferSize, r -> {
            AtomicInteger index = new AtomicInteger(1);
            return new Thread(null, r, "disruptor-thread-" + index.getAndIncrement());
        }, ProducerType.MULTI, new YieldingWaitStrategy());

        disruptor.handleEventsWith(logEventHandler);
        disruptor.start();
    }

    /**
     * 发布事件
     *
     * @param systemOptLogDto
     */
    public void publishEvent(SystemOptLogDTO systemOptLogDto) {
        final RingBuffer<LogEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new LogEventTranslator(), systemOptLogDto);
    }


    /**
     * disruptor shutdown
     */
    @Override
    public void destroy() throws Exception {
        disruptor.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!started.getAndSet(true)) {
            start(bufferSize);
            LogUtils.info(log, "disruptor startup successfully !");
        }
    }
}
