package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.wechat.WeChatMessage;
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
public class AskQuestionCommand extends AbstractCommand<CommandResult, WeChatMessage> {

    public AskQuestionCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(WeChatMessage weChatMessage) {
        String userCode = weChatMessage.getFromUserName();
        String toUserCode = weChatMessage.getToUserName();
        String content = weChatMessage.getContent();
        Long startTime = weChatMessage.getCreateTime();
        String result = executor.askAQuestion(startTime, userCode, toUserCode, content);
        return CommandResult.builder().message(result).build();
    }
}
