package com.idaymay.dzt.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在对话线程上记录当前用户，供 {@link DashScopeUserChatModelFactory} 解析用户级 DashScope API Key。
 */
@Component
@Slf4j
public class UserKeyStrategy {

    private static final ThreadLocal<String> USER_CODE = new ThreadLocal<>();

    public void setUserCode(String userCode) {
        USER_CODE.set(userCode);
    }

    public void clearUserCode() {
        USER_CODE.remove();
    }

    public String getUserCode() {
        return USER_CODE.get();
    }
}
