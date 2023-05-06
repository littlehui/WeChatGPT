package com.idaymay.dzt.service.command;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/30 16:44
 */
@Component
public class CheckQuestionNoCommand extends AbstractCommand<CommandResult>{

    public CheckQuestionNoCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, String toUserCode, String content) {
        String result = executor.checkQuestionNo(userCode);
        return CommandResult.builder()
                .message(result).build();
    }
}
