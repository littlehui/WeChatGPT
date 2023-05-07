package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.wechat.WeChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/30 16:58
 */
@Component
public class AnswerQuestionCommand extends AbstractCommand<CommandResult, WeChatMessage> {

    public AnswerQuestionCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(WeChatMessage weChatMessage) {
        //content是messageId
        String userCode = weChatMessage.getFromUserName();
        String content = weChatMessage.getContent();
        String result = executor.answerAQuestion(userCode, content);
        return CommandResult.builder().message(result).build();
    }
}
