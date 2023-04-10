package com.idaymay.dzt.app.web.config;

import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/10 23:22
 */
@Configuration
@ConfigurationProperties(prefix = "config.openai")
public class OpenAiConfig implements OpenAiConfigSupport {

    @Getter
    @Setter
    private String apiKey;

    @Override
    public String getApiKey() {
        return apiKey;
    }
}
