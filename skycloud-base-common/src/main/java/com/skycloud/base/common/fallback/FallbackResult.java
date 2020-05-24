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
package com.skycloud.base.common.fallback;

import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.SystemErrorCodeEnum;
import com.sky.framework.model.exception.BusinessException;

/**
 * @author
 */
public class FallbackResult {

    /**
     * 断路降级,优先显示错误系统异常信息
     *
     * @param cause
     * @return
     */
    public static MessageRes fail(Throwable cause) {
        if (cause instanceof BusinessException) {
            BusinessException be = (BusinessException) cause;
            return MessageRes.fail(be.getCode(), be.getMessage());
        }
        return MessageRes.fail(SystemErrorCodeEnum.GL990002.getCode(), SystemErrorCodeEnum.GL990002.getMsg());
    }
}
