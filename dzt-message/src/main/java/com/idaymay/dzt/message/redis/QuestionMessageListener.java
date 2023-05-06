package com.idaymay.dzt.message.redis;

import com.idaymay.dzt.bean.constant.MessageConstant;
import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.common.utils.obj.GsonUtil;
import com.idaymay.dzt.dao.redis.repository.AnswerCacheRepository;
import com.idaymay.dzt.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    @Autowired
    AnswerCacheRepository answerCacheRepository;

    @Autowired
    Redisson redisson;

    @Autowired
    ChatService chatService;

    @PostConstruct
    public void addListener() {
        redisson.getTopic(MessageConstant.CHAT_TOPIC).addListener(QuestionDTO.class, this);
    }

    @Override
    public void onMessage(CharSequence charSequence, QuestionDTO questionDTO) {
        log.info("receive message info : {}", GsonUtil.toJson(questionDTO));
        chatService.chat(questionDTO);
    }
}
