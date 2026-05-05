package com.idaymay.dzt.service;

import com.idaymay.dzt.bean.wechat.WeChatMessage;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2026/05/05 09:40
 */
public interface ChatStragey {

    WeChatMessage receiveAndChat(String messageId, WeChatMessage weChatMessage);
}
