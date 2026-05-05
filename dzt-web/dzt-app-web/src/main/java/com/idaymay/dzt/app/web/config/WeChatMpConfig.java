package com.idaymay.dzt.app.web.config;

import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "wx.mp")
@Data
public class WeChatMpConfig {

    private String appId;

    private String secret;

    private String token;

    private String aesKey;

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(appId);
        configStorage.setSecret(secret);
        if (StringUtils.hasText(token)) {
            configStorage.setToken(token);
        }
        if (StringUtils.hasText(aesKey)) {
            configStorage.setAesKey(aesKey);
        }
        return configStorage;
    }
}
