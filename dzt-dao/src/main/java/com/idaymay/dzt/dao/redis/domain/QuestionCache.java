package com.idaymay.dzt.dao.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:09
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCache implements Serializable {

    private String messageId;

    private String question;

    private Long askTimeMills;

    private String userCode;

    //第几次提问
    private Integer requestEdTimes;
}
