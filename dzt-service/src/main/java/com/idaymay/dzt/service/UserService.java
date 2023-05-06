package com.idaymay.dzt.service;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 18:10
 */
public interface UserService {

    /**
     * 初始化ApiKey
     * @param userCode
     * @param apiKey
     * @author littlehui
     * @date 2023/4/25 18:11
     * @return void
     */
    public void setOpenAiApiKey(String userCode, String apiKey);

}
