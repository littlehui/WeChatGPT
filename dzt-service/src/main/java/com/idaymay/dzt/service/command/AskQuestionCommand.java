package com.idaymay.dzt.service.command;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/30 17:00
 */
@Component
public class AskQuestionCommand extends AbstractCommand<CommandResult> {

    public AskQuestionCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, String toUserCode, String content) {
        String result = executor.askAQuestion(userCode, toUserCode, content);
        return CommandResult.builder().message(result).build();
    }
}
