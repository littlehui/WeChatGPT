package com.idaymay.dzt.service.command;

import com.idaymay.dzt.bean.constant.DztCommandConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 14:06
 */
@Slf4j
@Component
public class NoneCommand extends AbstractCommand<CommandResult> {

    public NoneCommand(DztCommandExecutor executor) {
        super(executor);
    }

    @Override
    public CommandResult execute(String userCode, List<String> args) {
        log.info("无效的command");
        return  CommandResult.builder()
                .message(DztCommandConstant.DEFAULT_RESULT_MESSAGE)
                .build();
    }
}
