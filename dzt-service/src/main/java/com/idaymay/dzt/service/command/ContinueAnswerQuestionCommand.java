package com.idaymay.dzt.service.command;

import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/30 17:19
 */
@Component
public class ContinueAnswerQuestionCommand extends AbstractCommand<CommandResult> {

    public ContinueAnswerQuestionCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, String toUser, String content) {
        String result = executor.answerAQuestion(userCode, content);
        return CommandResult.builder().message(result).build();
    }
}
