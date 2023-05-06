package com.idaymay.dzt.service.command;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/30 16:43
 */
@Component
public class CheckQuestionYesCommand extends AbstractCommand<CommandResult>{

    public CheckQuestionYesCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, String toUserCode, String content) {
        String result = executor.checkQuestionYes(userCode, toUserCode);
        return CommandResult.builder()
                .message(result).build();
    }
}
