package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.dao.redis.domain.ChatMessageCache;
import com.idaymay.dzt.dao.redis.optype.BaseRedisZSet;
import org.springframework.stereotype.Repository;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/16 00:37
 */
@Repository
public class ChatMessageRepository extends BaseRedisZSet<ChatMessageCache> {

    public ChatMessageRepository() {
        super();
        this.zone += "ChatMessage:";
    }
}
