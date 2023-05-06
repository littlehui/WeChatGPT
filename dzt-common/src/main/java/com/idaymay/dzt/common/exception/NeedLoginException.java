package com.idaymay.dzt.common.exception;

import com.idaymay.dzt.common.ajax.ResponseEnum;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/8/1 15:09
 */
public class NeedLoginException extends SystemException {

    public NeedLoginException(String msg) {
        super(msg);
        this.setCode(ResponseEnum.NEED_LOGIN.getCode());
    }
}
