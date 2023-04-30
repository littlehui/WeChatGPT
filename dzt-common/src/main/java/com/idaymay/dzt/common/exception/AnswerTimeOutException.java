package com.idaymay.dzt.common.exception;

import lombok.Data;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/29 21:19
 */
@Data
public class AnswerTimeOutException extends RuntimeException {

    private String messageId;

    private String userCode;

    private Integer timeOutCount;

    public AnswerTimeOutException(String messageId, String userCode, Integer timeOutCount) {
        this.messageId = messageId;
        this.userCode = userCode;
        this.timeOutCount = timeOutCount;
    }
}
