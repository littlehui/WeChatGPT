package com.idaymay.dzt.app.web.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * DashScope 通过 Spring {@link org.springframework.web.client.RestClient} 访问；Boot 默认工厂下读超时偏短，
 * 长推理易在收响应头阶段触发 {@link java.net.SocketTimeoutException}。
 * <p>通过 {@link ClientHttpRequestFactories} 与 {@link ClientHttpRequestFactorySettings} 配置超时，
 * 由 Boot 选择具体 {@link ClientHttpRequestFactory} 实现，避免直接使用已弃用的 {@code OkHttp3ClientHttpRequestFactory}。</p>
 */
@Configuration
public class DashScopeRestClientConfig {

    private static final int CONNECT_TIMEOUT_MS = 60_000;

    @Bean
    RestClientCustomizer dashScopeRestClientTimeouts(
            @Value("${spring.ai.dashscope.read-timeout:120000}") int readTimeoutMs) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MS))
                .withReadTimeout(Duration.ofMillis(readTimeoutMs));
        ClientHttpRequestFactory factory = ClientHttpRequestFactories.get(settings);
        return builder -> builder.requestFactory(factory);
    }
}
