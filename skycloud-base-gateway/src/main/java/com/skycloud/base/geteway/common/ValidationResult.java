package com.skycloud.base.geteway.common;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author
 */
@Data
public class ValidationResult {
    /**
     * 校验结果是否有错
     */
    private boolean hasErrors;

    /**
     * 校验错误信息
     */
    private List<String> errorMsg;


}
