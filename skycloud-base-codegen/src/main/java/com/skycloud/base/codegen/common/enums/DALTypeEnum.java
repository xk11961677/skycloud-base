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
package com.skycloud.base.codegen.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 数据库访问层类型
 *
 * @author
 */
public enum DALTypeEnum {

    TK_MYBATIS(0, "tk-mybatis"), MYBATIS_PLUS(1, "mybatis-plus");

    @Getter
    private int key;

    @Getter
    private String value;

    DALTypeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static DALTypeEnum acquire(final int key) {
        Optional<DALTypeEnum> serializeEnum =
                Arrays.stream(DALTypeEnum.values())
                        .filter(v -> Objects.equals(v.getKey(), key))
                        .findFirst();
        return serializeEnum.orElse(DALTypeEnum.TK_MYBATIS);

    }
}
