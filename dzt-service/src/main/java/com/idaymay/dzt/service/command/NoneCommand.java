package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.SystemCommandConstant;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 14:06
 */
@Slf4j
@Component
public class NoneCommand extends AbstractCommand<CommandResult, WeChatMessage> {

    public NoneCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(WeChatMessage weChatMessage) {
        String userCode = weChatMessage.getFromUserName();
        log.info("无效的command");
        return  CommandResult.builder()
                .message(SystemCommandConstant.DEFAULT_RESULT_MESSAGE)
                .build();
    }
}
