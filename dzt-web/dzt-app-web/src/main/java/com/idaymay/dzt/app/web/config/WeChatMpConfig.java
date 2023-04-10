package com.idaymay.dzt.app.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@ConfigurationProperties(prefix = "config.wechat.mp")
//@Data
public class WeChatMpConfig {

    private String appId;

    private String appSecret;

    private String token;

    private String encodingAesKey;
}
