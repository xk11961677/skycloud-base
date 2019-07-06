package com.skycloud.base.authorization.exception;

import com.alibaba.fastjson.JSONObject;
import com.sky.framework.model.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public class AuzBussinessException extends BusinessException {

    @Getter
    @Setter
    private JSONObject data;

    /**
     * Instantiates a new Auz rpc exception.
     */
    public AuzBussinessException() {
    }

    /**
     * Instantiates a new Auz rpc exception.
     *
     * @param code      the code
     * @param msgFormat the msg format
     * @param args      the args
     */
    public AuzBussinessException(int code, String msgFormat, Object... args) {
        super(code, msgFormat, args);
    }

    /**
     * Instantiates a new Auz rpc exception.
     *
     * @param code the code
     * @param msg  the msg
     */
    public AuzBussinessException(int code, String msg) {
        super(code, msg);
    }

    public AuzBussinessException(AuthErrorType authErrorType, JSONObject data) {
        super(authErrorType.getCode(), authErrorType.getMsg());
        this.data = data;
    }

    public AuzBussinessException(AuthErrorType authErrorType) {
        super(authErrorType.getCode(), authErrorType.getMsg());
    }
}
