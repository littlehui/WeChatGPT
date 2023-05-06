package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.bean.constant.SystemCommandConstant;
import com.idaymay.dzt.dao.redis.repository.CurrentQuestionCheckRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 14:03
 */
@Component
public class CommandFactory {

    @Resource
    SetApiKeyCommand setApiKeyCommand;

    @Resource
    CheckQuestionNoCommand checkQuestionNoCommand;

    @Resource
    CheckQuestionYesCommand checkQuestionYesCommand;

    @Resource
    NoneCommand noneCommand;

    @Resource
    AnswerQuestionCommand answerQuestionCommand;

    @Resource
    AskQuestionCommand askQuestionCommand;

    @Resource
    CurrentQuestionCheckRepository currentQuestionCheckRepository;

    @Resource
    ContinueAnswerQuestionCommand continueAnswerQuestionCommand;

    public Command createSystemCommand(String commandName) {
        switch (commandName) {
            case SystemCommandConstant.SET_APIKEY:
                return setApiKeyCommand;
            default:
                return noneCommand;
        }
    }

    public Command createChatCommand(String userCode, String content) {
        String commandName = content;
        if (content.startsWith(SystemCommandConstant.COMMAND_PRE)) {
            //命令开头 setApiKey xxxxxx
            String[] args = content.split(" ");
            commandName = args[0];
            List<String> commandArgs = new ArrayList<String>();
            for (int i = 0; i < args.length ; i++) {
                if (i>0) {
                    commandArgs.add(args[i]);
                }
            }
            Command command = createSystemCommand(commandName);
            return command;
        }
        if (content.startsWith(ChatConstants.ANSWER_PRE)) {
            return answerQuestionCommand;
        }
        String currentCheckQuestion = currentQuestionCheckRepository.get(userCode);
        switch (content) {
            case ChatConstants.QUESTION_CHECK_RESULT_YES:
                if (currentCheckQuestion != null) {
                    return checkQuestionYesCommand;
                } else {
                    return askQuestionCommand;
                }
            case ChatConstants.QUESTION_CHECK_RESULT_NO:
                if (currentCheckQuestion != null) {
                    return checkQuestionNoCommand;
                } else {
                    return askQuestionCommand;
                }
            case ChatConstants.ANSWER_CONTINUE:
                return continueAnswerQuestionCommand;
            default:
                return askQuestionCommand;
        }
    }
}
