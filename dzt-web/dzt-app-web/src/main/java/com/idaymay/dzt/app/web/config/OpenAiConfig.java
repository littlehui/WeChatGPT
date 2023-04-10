package com.idaymay.dzt.app.web.config;

import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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
}
