package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.dao.redis.repository.CurrentAnswerQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/30 17:19
 */
@Component
public class ContinueAnswerQuestionCommand extends AbstractCommand<CommandResult, WeChatMessage> {

    public ContinueAnswerQuestionCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(WeChatMessage weChatMessage) {
        String userCode = weChatMessage.getFromUserName();
        String result = executor.continueAnswer(userCode);
        return CommandResult.builder().message(result).build();
    }
}
