package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.common.utils.number.RandomUtil;
import com.idaymay.dzt.dao.redis.repository.CurrentQuestionCheckRepository;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 11:31
 */
@Component
@Slf4j
public class DztCommandExecutor {

    @Autowired
    UserService userService;

    @Autowired
    ChatService chatService;

    @Autowired
    CurrentQuestionCheckRepository currentQuestionCheckRepository;

    public void setUserApiKey(String user, String apiKey) {
        userService.setOpenAiApiKey(user, apiKey);
        log.info("设置openAiApikey成功：{},{}", user, apiKey);
    }

    public String checkQuestionYes(String fromUserName, String toUserName) {
        String currentCheckQuestion = currentQuestionCheckRepository.get(fromUserName);
        String answer = chatService.askAQuestion(currentCheckQuestion, fromUserName, toUserName);
        //非 REPEAT_PRE开头的，说明正常返回。清空一下check缓存
        if (!answer.startsWith(ChatConstants.REPEAT_PRE)) {
            currentQuestionCheckRepository.delete(fromUserName);
        }
        return answer;
    }

    public String checkQuestionNo(String userCode) {
        return String.format(ChatConstants.REPEAT_QUESTION_ANSWER_NO[RandomUtil.randomInt(0, 3)]);
    }

    public String answerAQuestion(String userCode, String messageId) {
        String answer = chatService.answerAQuestion(userCode, messageId);
        return answer;
    }

    public String askAQuestion(String userCode, String toUserCode, String content) {
        return chatService.askAQuestion(content, userCode, toUserCode);
    }

    public String continueAnswer(String userCode) {
        return chatService.continueAnswer(userCode);
    }
}
