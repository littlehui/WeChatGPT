package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.service.OpenAiService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Override
    public String chat(String promot) {
        List<String> apiKeys = new ArrayList<String>();
        apiKeys.add("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");
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(apiKeys)
                .apiHost("https://api.openai.com/v1/models/")
                .okHttpClient(new OkHttpClient())
                .build();
        ChatCompletion chatCompletion = new ChatCompletion();
        List<Message> messages = new ArrayList<>();
        Message message = new Message();
        message.setContent(promot);
        message.setRole(Message.Role.USER.getName());
        message.setName("blues");
        messages.add(message);
        chatCompletion.setMessages(messages);
        ChatCompletionResponse response = openAiClient.chatCompletion(chatCompletion);
        return response.getObject();
    }
}
