package com.idaymay.dzt.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import com.idaymay.dzt.service.OpenAiService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService, ApplicationContextAware {

    ApplicationContext context;

    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    OpenAiClient openAiClient;

    @Override
    public String chat(String promot, String name) {
        ChatCompletion chatCompletion = new ChatCompletion();
        List<Message> messages = new ArrayList<>();
        Message message = new Message();
        message.setContent(promot);
        message.setRole(Message.Role.USER.getName());
        //message.setName("blues");
        messages.add(message);
        chatCompletion.setMessages(messages);
        ChatCompletionResponse response = openAiClient.chatCompletion(chatCompletion);
        log.info("chat response:{}", response);
        String answer = "";
        if (response != null && response.getChoices() != null && response.getChoices().size() > 0) {
            ChatChoice choice = response.getChoices().get(0);
            log.info("choice != null");
            if (choice.getMessage() != null) {
                answer = choice.getMessage().getContent();
            } else {
                log.warn("answer message is null");
            }
        } else {
            log.info("choice is null");
        }
        return answer;
    }

    @Override
    public String quickAnswer(String promot, String name) {
        Long messageId = snowflakeIdGenerator.nextId();
        redisTemplate.opsForValue().set(messageId, promot);
        return messageId + "";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
