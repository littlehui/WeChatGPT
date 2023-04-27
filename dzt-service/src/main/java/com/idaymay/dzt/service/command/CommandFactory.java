package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.DztCommandConstant;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 14:03
 */
@Component
public class CommandFactory {

    @Resource
    SetApiKeyCommand setApiKeyCommand;

    @Resource
    NoneCommand noneCommand;

    public Command createCommand(String commandName) {
        switch (commandName) {
            case DztCommandConstant.SET_APIKEY:
                return setApiKeyCommand;
            default:
                return noneCommand;
        }
    }
}
