package com.idaymay.dzt.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.idaymay.dzt.common.servlet.WebContext;
import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import com.idaymay.dzt.dao.redis.repository.UserConfigCacheRepository;
import com.unfbx.chatgpt.function.KeyStrategyFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 18:01
 */
@Component
@Slf4j
public class UserKeyStrategy implements KeyStrategyFunction<List<String>, String> {

    @Autowired
    UserConfigCacheRepository userConfigCacheRepository;

    private static ThreadLocal<String> userThreadLocal = new ThreadLocal<>();

    public void setUserCode(String userCode) {
        userThreadLocal.set(userCode);
    }

    private String getUserCode() {
        return userThreadLocal.get();
    }

    @Override
    public String apply(List<String> strings) {
        String userCode = getUserCode();
        if (userCode != null) {
            UserConfigCache userConfigCache = userConfigCacheRepository.getUserConfig(userCode);
            if (userConfigCache != null) {
                return userConfigCache.getOpenAiApiKey();
            } else {
                return RandomUtil.randomEle(strings);
            }
        } else {
            log.warn("userCode为null。");
        }
        return RandomUtil.randomEle(strings);
    }
}
