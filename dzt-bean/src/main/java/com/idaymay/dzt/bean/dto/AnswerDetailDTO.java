package com.idaymay.dzt.bean.dto;

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
 * @date 2023/04/11 16:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDetailDTO implements Serializable {

   private String messageId;

   private String userCode;

   private String question;

   private String answer;

   private Long askTimeMills;

   private Long answerTimeMills;
}
