package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import com.idaymay.dzt.service.OpenAiService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiServiceImpl implements OpenAiService, ApplicationContextAware {

    @Autowired
    ApplicationContext context;

    @Override
    public String chat(String promot) {
        List<String> apiKeys = new ArrayList<String>();
        apiKeys.add(context.getBean(OpenAiConfigSupport.class).getApiKey());
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(apiKeys)
                .okHttpClient(new OkHttpClient())
                .build();
        ChatCompletion chatCompletion = new ChatCompletion();
        List<Message> messages = new ArrayList<>();
        Message message = new Message();
        message.setContent(promot);
        message.setRole(Message.Role.USER.getName());
        //message.setName("blues");
        messages.add(message);
        chatCompletion.setMessages(messages);
        ChatCompletionResponse response = openAiClient.chatCompletion(chatCompletion);
        return response.getObject();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
