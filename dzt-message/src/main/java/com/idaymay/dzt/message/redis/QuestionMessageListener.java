package com.idaymay.dzt.message.redis;

import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.common.utils.obj.GsonUtil;
import com.idaymay.dzt.common.utils.string.StringUtil;
import com.idaymay.dzt.dao.redis.domain.AnswerCache;
import com.idaymay.dzt.dao.redis.repository.AnswerCacheRepository;
import com.idaymay.dzt.service.OpenAiService;
import com.idaymay.dzt.service.constant.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 23:11
 */
@Slf4j
@Component
public class QuestionMessageListener implements MessageListener<QuestionDTO> {

    @Resource
    OpenAiService openAiService;

    @Resource
    AnswerCacheRepository answerCacheRepository;

    @Resource
    Redisson redisson;

    @PostConstruct
    public void addListener() {
        redisson.getTopic(MessageConstant.CHAT_TOPIC).addListener(QuestionDTO.class, this);
    }

    @Override
    public void onMessage(CharSequence charSequence, QuestionDTO questionDTO) {
        log.info("receive message info : {}", GsonUtil.toJson(questionDTO));
        String answer = openAiService.chat(questionDTO.getQuestion(), questionDTO.getUser());
        AnswerCache answerCache = AnswerCache.builder()
                .answer(answer)
                .messageId(questionDTO.getMessageId())
                .question(questionDTO.getQuestion())
                .askTimeMills(questionDTO.getAskTimeMills())
                .answerTimeMills(System.currentTimeMillis())
                .name(questionDTO.getUser())
                .answerSegment(Arrays.asList(StringUtil.foldString(answer, 500)))
                .currentSegment(0)
                .build();
        answerCacheRepository.saveAnswer(answerCache);
        log.info("question {}, answered!", questionDTO.getMessageId());
    }
}
