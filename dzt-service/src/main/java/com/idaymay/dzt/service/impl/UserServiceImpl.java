package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import com.idaymay.dzt.dao.redis.repository.UserConfigCacheRepository;
import com.idaymay.dzt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 18:11
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserConfigCacheRepository userConfigCacheRepository;

    @Override
    public void setOpenAiApiKey(String userCode, String apiKey) {
        UserConfigCache userConfigCache = UserConfigCache.builder()
                .openAiApiKey(apiKey)
                .userCode(userCode)
                .build();
        userConfigCacheRepository.saveUserConfig(userConfigCache);
    }
}
