package com.idaymay.dzt.service;

public interface OpenAiService {

    /**
     * 与chatGpt对话
     * TODO
     * @param promot
     * @param name
     * @author littlehui
     * @date 2023/4/11 12:47
     * @return java.lang.String
     */
    public String chat(String promot, String name);

    public String quickAnswer(String promot, String name);

}
