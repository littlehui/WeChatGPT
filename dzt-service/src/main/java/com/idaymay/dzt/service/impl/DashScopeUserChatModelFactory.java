package com.idaymay.dzt.service.impl;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.idaymay.dzt.common.utils.string.StringUtil;
import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import com.idaymay.dzt.dao.redis.repository.UserConfigCacheRepository;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 按用户 Redis 中的 API Key（沿用 {@link UserConfigCache#getOpenAiApiKey()} 字段，语义为 DashScope Key）选择 {@link ChatModel}；
 * 无用户 Key 时复用 Spring Boot 自动配置的 {@link DashScopeChatModel}。
 * <p>合并连接信息通过 {@link DashScopeConnectionUtils} 解析；为避免在源码中直接依赖 Java {@code record} 类型（便于父 POM 仍以 1.8 编译），解析结果用反射取出。</p>
 */
@Component
@Slf4j
public class DashScopeUserChatModelFactory {

    private final DashScopeChatModel defaultDashScopeChatModel;
    private final DashScopeConnectionProperties connectionProperties;
    private final DashScopeChatProperties chatProperties;
    private final UserConfigCacheRepository userConfigCacheRepository;
    private final ObjectProvider<RestClient.Builder> restClientBuilderProvider;
    private final ObjectProvider<WebClient.Builder> webClientBuilderProvider;
    private final ObjectProvider<ResponseErrorHandler> responseErrorHandlerProvider;
    private final RetryTemplate retryTemplate;
    private final ToolCallingManager toolCallingManager;
    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;
    private final ObjectProvider<ToolExecutionEligibilityPredicate> toolExecutionEligibilityPredicateProvider;

    private String mergedBaseUrl;
    private String mergedWorkspaceId;
    private MultiValueMap<String, String> mergedHeaders;

    public DashScopeUserChatModelFactory(
            DashScopeChatModel defaultDashScopeChatModel,
            DashScopeConnectionProperties connectionProperties,
            DashScopeChatProperties chatProperties,
            UserConfigCacheRepository userConfigCacheRepository,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<ResponseErrorHandler> responseErrorHandlerProvider,
            RetryTemplate retryTemplate,
            ToolCallingManager toolCallingManager,
            ObjectProvider<ObservationRegistry> observationRegistryProvider,
            ObjectProvider<ToolExecutionEligibilityPredicate> toolExecutionEligibilityPredicateProvider) {
        this.defaultDashScopeChatModel = defaultDashScopeChatModel;
        this.connectionProperties = connectionProperties;
        this.chatProperties = chatProperties;
        this.userConfigCacheRepository = userConfigCacheRepository;
        this.restClientBuilderProvider = restClientBuilderProvider;
        this.webClientBuilderProvider = webClientBuilderProvider;
        this.responseErrorHandlerProvider = responseErrorHandlerProvider;
        this.retryTemplate = retryTemplate;
        this.toolCallingManager = toolCallingManager;
        this.observationRegistryProvider = observationRegistryProvider;
        this.toolExecutionEligibilityPredicateProvider = toolExecutionEligibilityPredicateProvider;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    void initResolved() {
        try {
            Object resolved = DashScopeConnectionUtils.resolveConnectionProperties(
                    connectionProperties, chatProperties, "chat");
            Class<?> cl = resolved.getClass();
            mergedBaseUrl = (String) cl.getMethod("baseUrl").invoke(resolved);
            mergedWorkspaceId = (String) cl.getMethod("workspaceId").invoke(resolved);
            mergedHeaders = (MultiValueMap<String, String>) cl.getMethod("headers").invoke(resolved);
        } catch (Exception e) {
            throw new IllegalStateException("DashScope connection resolve failed", e);
        }
    }

    /**
     * 与原先 {@code openAiClient.chatCompletion} 同线程语义：在调用前由业务设置 userCode。
     */
    public ChatResponse call(String userCode, Prompt prompt) {
        return chatModelForUser(userCode).call(prompt);
    }

    public ChatModel chatModelForUser(String userCode) {
        String userKey = resolveUserDashScopeApiKey(userCode);
        if (StringUtil.isEmpty(userKey)) {
            return defaultDashScopeChatModel;
        }
        return buildModelWithApiKey(userKey);
    }

    private String resolveUserDashScopeApiKey(String userCode) {
        if (StringUtil.isEmpty(userCode)) {
            return null;
        }
        UserConfigCache cache = userConfigCacheRepository.getUserConfig(userCode);
        if (cache == null) {
            return null;
        }
        return cache.getOpenAiApiKey();
    }

    private DashScopeChatModel buildModelWithApiKey(String apiKey) {
        RestClient.Builder rb = restClientBuilderProvider.getIfAvailable(RestClient::builder);
        if (rb == null) {
            rb = RestClient.builder();
        }
        WebClient.Builder wb = webClientBuilderProvider.getIfAvailable(WebClient::builder);
        if (wb == null) {
            wb = WebClient.builder();
        }
        ResponseErrorHandler errorHandler = responseErrorHandlerProvider.getIfAvailable();
        if (errorHandler == null) {
            errorHandler = new org.springframework.web.client.DefaultResponseErrorHandler();
        }
        DashScopeApi api = DashScopeApi.builder()
                .apiKey(apiKey)
                .headers(mergedHeaders)
                .baseUrl(mergedBaseUrl)
                .workSpaceId(mergedWorkspaceId)
                .webClientBuilder(wb)
                .restClientBuilder(rb)
                .responseErrorHandler(errorHandler)
                .build();
        DashScopeChatOptions options = chatProperties.getOptions();
        if (options == null) {
            options = new DashScopeChatOptions();
        }
        ObservationRegistry observationRegistry = observationRegistryProvider.getIfAvailable(() -> ObservationRegistry.NOOP);
        ToolExecutionEligibilityPredicate toolPredicate = toolExecutionEligibilityPredicateProvider.getIfAvailable(
                () -> (co, cr) -> false);
        return DashScopeChatModel.builder()
                .dashScopeApi(api)
                .retryTemplate(retryTemplate)
                .toolCallingManager(toolCallingManager)
                .defaultOptions(options)
                .observationRegistry(observationRegistry)
                .toolExecutionEligibilityPredicate(toolPredicate)
                .build();
    }
}
