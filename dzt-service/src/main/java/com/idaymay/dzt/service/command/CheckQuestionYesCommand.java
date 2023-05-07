package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.wechat.WeChatMessage;
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
public class CheckQuestionYesCommand extends AbstractCommand<CommandResult, WeChatMessage>{

    public CheckQuestionYesCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(WeChatMessage weChatMessage) {
        String userCode = weChatMessage.getFromUserName();
        String toUserCode = weChatMessage.getToUserName();
        String result = executor.checkQuestionYes(userCode, toUserCode);
        return CommandResult.builder()
                .message(result).build();
    }
}
