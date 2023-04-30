package com.idaymay.dzt.service;

import com.idaymay.dzt.bean.dto.QuestionDTO;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 16:02
 */
public interface ChatService {

   /**
    * 快速回答，返回消息ID
    * @param question
    * @param fromUser
    * @param toUser
    * @author littlehui
    * @date 2023/4/11 16:03
    * @return java.lang.String
    */
   public String askAQuestion(String question, String fromUser, String toUser);

   /**
    * 返回已经处理好的回答
    * @param messageId
    * @param userCode
    * @author littlehui
    * @date 2023/4/11 16:04
    * @return java.lang.String
    */
   public String answerAQuestion(String userCode, String messageId);

   /**
    * 包含上下文
    * @param questionDTO
    * @param associationRound
    * @author littlehui
    * @date 2023/4/16 01:36
    * @return java.lang.String
    */
   public String chat(QuestionDTO questionDTO, Long associationRound);

   /**
    * 默认上下文聊天方式
    * @param questionDTO
    * @author littlehui
    * @date 2023/4/16 18:49
    * @return java.lang.String
    */
   public String chat(QuestionDTO questionDTO);

   /**
    * 继续回答的实现
    * @param userCode
    * @author littlehui
    * @date 2023/4/29 20:13
    * @return java.lang.String
    */
   public String continueAnswer(String userCode);
}
