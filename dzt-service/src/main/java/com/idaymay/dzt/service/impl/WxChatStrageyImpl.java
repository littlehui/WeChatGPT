package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.common.redission.CustomRedissonLock;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.ChatStragey;
import com.idaymay.dzt.service.command.Command;
import com.idaymay.dzt.service.command.CommandFactory;
import com.idaymay.dzt.service.command.CommandResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2026/05/05 09:40
 */
@Slf4j
@Service
public class WxChatStrageyImpl implements ChatStragey {

    @Resource
    CommandFactory commandFactory;

    @CustomRedissonLock(lockIndex = 0, leaseTime = 5)
    @Override
    public WeChatMessage receiveAndChat(String messageId, WeChatMessage weChatMessage) {
        log.info("messge 接收到：{}", weChatMessage);
        String content = weChatMessage.getContent();
        String fromUserName = weChatMessage.getFromUserName();
        String toUserName = weChatMessage.getToUserName();
        //新建一个响应对象
        WeChatMessage responseMessage = new WeChatMessage();
        //消息来自谁
        responseMessage.setFromUserName(toUserName);
        //消息发送给谁
        responseMessage.setToUserName(fromUserName);
        //消息类型，返回的是文本
        responseMessage.setMsgType("text");
        //消息创建时间，当前时间就可以
        responseMessage.setCreateTime(System.currentTimeMillis());
        Command command = commandFactory.createChatCommand(fromUserName, content);
        CommandResult commandResult = command.execute(weChatMessage);
        responseMessage.setContent(commandResult.getMessage());
        return responseMessage;
    }
}
