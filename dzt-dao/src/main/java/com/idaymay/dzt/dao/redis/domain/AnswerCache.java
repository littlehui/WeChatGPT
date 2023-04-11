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
 * @date 2023/04/11 21:08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCache implements Serializable {

    private String messageId;

    private String name;

    private String question;

    private String answer;

    private Long askTimeMills;

    private Long answerTimeMills;
}
