package com.idaymay.dzt.service.command;

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
public class AnswerQuestionCommand extends AbstractCommand<CommandResult> {

    public AnswerQuestionCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, String toUserCode, String content) {
        //content是messageId
        String result = executor.answerAQuestion(userCode, content);
        return CommandResult.builder().message(result).build();
    }
}
