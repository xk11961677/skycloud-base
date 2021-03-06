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
package com.skycloud.base.log.manager.disruptor.translator;

import com.skycloud.base.log.api.model.dto.SystemOptLogDTO;
import com.skycloud.base.log.manager.disruptor.event.LogEvent;
import com.skycloud.base.log.model.po.SystemLog;
import com.lmax.disruptor.EventTranslatorOneArg;

import java.util.Date;
import java.util.Optional;

/**
 * @author
 */
public class LogEventTranslator implements EventTranslatorOneArg<LogEvent, SystemOptLogDTO> {

    public LogEventTranslator() {
    }

    @Override
    public void translateTo(LogEvent logEvent, long l, SystemOptLogDTO systemOptLogDto) {
        SystemLog systemLog = new SystemLog();
        systemLog.setBizType(systemOptLogDto.getBizType().getKey());
        systemLog.setType(systemOptLogDto.getType().getKey());
        systemLog.setRemark(systemOptLogDto.getRemark());
        systemLog.setCreateBy(Optional.ofNullable(systemOptLogDto.getCreateBy()).orElse("system"));
        systemLog.setCreateTime(new Date());
        systemLog.setDisabled(0);

        logEvent.setSystemLog(systemLog);

    }
}
