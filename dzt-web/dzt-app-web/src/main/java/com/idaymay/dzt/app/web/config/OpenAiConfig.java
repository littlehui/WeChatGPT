package com.idaymay.dzt.app.web.config;

import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import com.unfbx.chatgpt.OpenAiClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/10 23:22
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "config.openai")
public class OpenAiConfig implements OpenAiConfigSupport {

    @Getter
    @Setter
    private String apiKey;

    @PostConstruct
    public void openAiConfigCreated() {
        log.info("openAiConfig created!");
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Bean
    public OpenAiClient openAiClient() {
        List<String> apiKeys = new ArrayList<String>();
        apiKeys.add(apiKey);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .callTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(apiKeys)
                .okHttpClient(okHttpClient)
                .build();
        return openAiClient;
    }
}
