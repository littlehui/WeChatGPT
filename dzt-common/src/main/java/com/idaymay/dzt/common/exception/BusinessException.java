package com.idaymay.dzt.common.exception;


import com.idaymay.dzt.common.ajax.ResponseEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends Exception {

    private Integer code;

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Integer code, Throwable e) {
        super(message, e);
        this.code = code;
    }

    public BusinessException(ResponseEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

}
