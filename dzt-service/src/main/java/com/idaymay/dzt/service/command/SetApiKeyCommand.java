package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.SystemCommandConstant;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 11:32
 */
@Component
public class SetApiKeyCommand extends AbstractCommand<CommandResult, WeChatMessage> {

    private static final String SET_API_KEY_SUCCESS = "设置ApiKey成功,您可以正常使用了。";

    public SetApiKeyCommand(@Autowired DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(WeChatMessage weChatMessage) {
        String content = weChatMessage.getContent();
        String userCode = weChatMessage.getFromUserName();
        //命令开头 setApiKey xxxxxx
        String[] args = content.split(" ");
        String commandName = args[0];
        List<String> commandArgs = new ArrayList<String>();
        for (int i = 0; i < args.length ; i++) {
            if (i>0) {
                commandArgs.add(args[i]);
            }
        }
        if (commandArgs != null && commandArgs.size() > 0) {
            String apiKey = commandArgs.get(0);
            executor.setUserApiKey(userCode, apiKey);
        } else {
            return CommandResult.builder().message(SystemCommandConstant.DEFAULT_RESULT_MESSAGE).build();
        }
        return CommandResult.builder().message(SET_API_KEY_SUCCESS).build();
    }
}
