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
