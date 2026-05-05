package com.idaymay.dzt.app.web.service;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import com.idaymay.dzt.dao.redis.repository.AnswerCacheRepository;
import com.idaymay.dzt.dao.redis.repository.ChatMessageRepository;
import com.idaymay.dzt.dao.redis.repository.CurrentAnswerQuestionRepository;
import com.idaymay.dzt.dao.redis.repository.FreeCountCacheRepository;
import com.idaymay.dzt.dao.redis.repository.UserConfigCacheRepository;
import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import com.idaymay.dzt.service.impl.ChatServiceImpl;
import com.idaymay.dzt.service.impl.DashScopeUserChatModelFactory;
import com.idaymay.dzt.service.impl.LlmHttpStatusUtil;
import com.idaymay.dzt.service.impl.UserKeyStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ChatServiceImpl} 在接入 DashScope / Spring AI 后的对话与错误分支单测（位于 app-web 模块）。
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceImplDashScopeTest {

    @Mock
    private DashScopeUserChatModelFactory dashScopeUserChatModelFactory;
    @Mock
    private UserConfigCacheRepository userConfigCacheRepository;
    @Mock
    private FreeCountCacheRepository freeCountCacheRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private AnswerCacheRepository answerCacheRepository;
    @Mock
    private CurrentAnswerQuestionRepository currentAnswerQuestionRepository;
    @Mock
    private OpenAiConfigSupport openAiConfigSupport;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    void wireManualFields() {
        ReflectionTestUtils.setField(chatService, "userKeyStrategy", new UserKeyStrategy());
        ReflectionTestUtils.setField(chatService, "openAiConfigSupport", openAiConfigSupport);
    }

    @Test
    void chat_success_incrementsFreeCountWhenNoUserApiKey() {
        when(chatMessageRepository.latest(anyString(), any())).thenReturn(new LinkedHashSet<>());
        when(userConfigCacheRepository.getUserConfig("u1")).thenReturn(null);
        ChatResponse ok = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage("hello"))));
        when(dashScopeUserChatModelFactory.call(eq("u1"), any(Prompt.class))).thenReturn(ok);

        QuestionDTO dto = QuestionDTO.builder()
                .userCode("u1")
                .messageId("m1")
                .question("q?")
                .askTimeMills(System.currentTimeMillis())
                .requestTimes(1)
                .build();

        assertEquals("hello", chatService.chat(dto, 2L));
        verify(freeCountCacheRepository, times(1)).incrUsedFreeCount("u1");
    }

    @Test
    void chat_success_skipsFreeCountWhenUserHasApiKey() {
        when(chatMessageRepository.latest(anyString(), any())).thenReturn(new LinkedHashSet<>());
        UserConfigCache cfg = new UserConfigCache();
        cfg.setOpenAiApiKey("dashscope-user-key");
        when(userConfigCacheRepository.getUserConfig("u1")).thenReturn(cfg);
        ChatResponse ok = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage("hi"))));
        when(dashScopeUserChatModelFactory.call(eq("u1"), any(Prompt.class))).thenReturn(ok);

        QuestionDTO dto = QuestionDTO.builder()
                .userCode("u1")
                .messageId("m1")
                .question("q?")
                .askTimeMills(System.currentTimeMillis())
                .requestTimes(1)
                .build();

        assertEquals("hi", chatService.chat(dto, 2L));
        verify(freeCountCacheRepository, never()).incrUsedFreeCount(anyString());
    }

    @Test
    void chat_http401_returnsApiKeyError() {
        when(chatMessageRepository.latest(anyString(), any())).thenReturn(new LinkedHashSet<>());
        when(dashScopeUserChatModelFactory.call(eq("u1"), any(Prompt.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "unauthorized"));

        QuestionDTO dto = QuestionDTO.builder()
                .userCode("u1")
                .messageId("m1")
                .question("q?")
                .askTimeMills(System.currentTimeMillis())
                .requestTimes(1)
                .build();

        assertEquals(ChatConstants.API_KEY_ERROR, chatService.chat(dto, 2L));
        verify(answerCacheRepository, times(1)).saveAnswer(any());
    }

    @Test
    void chat_http400_retriesSecondCallWithConfiguredMinusOneRound() {
        when(chatMessageRepository.latest(anyString(), any())).thenReturn(new LinkedHashSet<>());
        when(openAiConfigSupport.getAssociationRound()).thenReturn(3L);
        ChatResponse ok = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage("retry-ok"))));
        when(dashScopeUserChatModelFactory.call(eq("u1"), any(Prompt.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "context"))
                .thenReturn(ok);

        QuestionDTO dto = QuestionDTO.builder()
                .userCode("u1")
                .messageId("m1")
                .question("q?")
                .askTimeMills(System.currentTimeMillis())
                .requestTimes(1)
                .build();

        assertEquals("retry-ok", chatService.chat(dto, 2L));

        // 与历史实现一致：400 后递归使用 openAiConfigSupport.getAssociationRound()-1（此处为 2），故两次 latest 的乘数均为 2*2=4
        verify(chatMessageRepository, times(2)).latest(eq("u1"), eq(4L));
        verify(dashScopeUserChatModelFactory, times(2)).call(eq("u1"), any(Prompt.class));
    }

    @Test
    void llmHttpStatusUtil_findsNestedClientError() {
        Throwable root = new RuntimeException(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "x"));
        assertEquals(400, LlmHttpStatusUtil.resolveHttpStatus(root));
    }
}
