package com.idaymay.dzt.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description TODO
 * @ClassName SystemException
 * @Author littlehui
 * @Date 2021/6/30 16:03
 * @Version 1.0
 **/
public class SystemException extends RuntimeException {

    @Setter
    @Getter
    private Integer code;

    public SystemException(String message) {
        super(message);
    }
}
