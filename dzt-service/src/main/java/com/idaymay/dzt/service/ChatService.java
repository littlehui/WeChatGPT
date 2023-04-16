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
    * @param user
    * @author littlehui
    * @date 2023/4/11 16:03
    * @return java.lang.String
    */
   public String askAQuestion(String question, String user);

   /**
    * 返回已经处理好的回答
    * @param messageId
    * @author littlehui
    * @date 2023/4/11 16:04
    * @return java.lang.String
    */
   public String answerAQuestion(String messageId);

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

}
