package com.idaymay.dzt.common.exception;

import com.idaymay.dzt.bean.wechat.WeChatMessage;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/24 10:41
 */
public class RateLimitException extends RuntimeException {

    private String toUserName;

    private String fromUserName;

    private String message;

    public RateLimitException(String toUser, String fromUser, String message) {
        this.toUserName = toUser;
        this.fromUserName = fromUser;
        this.message = message;
    }

    public WeChatMessage getWeChatMessage () {
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
        responseMessage.setContent(message);
        return responseMessage;
    }
}
