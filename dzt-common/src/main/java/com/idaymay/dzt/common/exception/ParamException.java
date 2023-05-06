package com.idaymay.dzt.common.exception;

import com.idaymay.dzt.common.ajax.ResponseEnum;

/**
 * @Description TODO
 * @ClassName ParamException
 * @Author littlehui
 * @Date 2021/7/3 19:00
 * @Version 1.0
 **/
public class ParamException extends RuntimeException {

    private Integer code;

    public ParamException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public ParamException(String message) {
        super(message);
        this.code = ResponseEnum.PARAM_INVALID.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
