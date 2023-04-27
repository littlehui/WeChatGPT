package com.idaymay.dzt.service.command;

import com.idaymay.dzt.dao.redis.repository.UserConfigCacheRepository;
import com.idaymay.dzt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 11:31
 */
@Component
@Slf4j
public class DztCommandExecutor {

    @Autowired
    UserService userService;

    public void setUserApiKey(String user, String apiKey) {
        userService.setOpenAiApiKey(user, apiKey);
        log.info("设置openAiApikey成功：{},{}", user, apiKey);
    }
}
