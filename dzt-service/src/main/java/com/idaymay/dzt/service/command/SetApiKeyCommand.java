package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.DztCommandConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 11:32
 */
@Component
public class SetApiKeyCommand extends AbstractCommand<CommandResult> {

    private static final String SET_API_KEY_SUCCESS = "设置ApiKey成功,您可以正常使用了。";

    public SetApiKeyCommand(@Autowired DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, List<String> args) {
        if (args != null && args.size() > 0) {
            String apiKey = args.get(0);
            executor.setUserApiKey(userCode, apiKey);
        } else {
            return CommandResult.builder().message(DztCommandConstant.DEFAULT_RESULT_MESSAGE).build();
        }
        return CommandResult.builder().message(SET_API_KEY_SUCCESS).build();
    }
}
