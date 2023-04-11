package com.idaymay.dzt.bean.wechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
//指定xml的根节点
@XmlRootElement(name = "xml")
//指定Xml映射的生效范围
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeChatMessage {
    /**
     * 开发者微信号
     */
    //指定Xml映射节点名
    @XmlElement(name = "ToUserName")
    protected String toUserName;
    /**
     * 发送方账号（一个OpenID）
     */
    @XmlElement(name = "FromUserName")
    protected String fromUserName;
    /**
     * 消息类型，文本为text
     */
    @XmlElement(name = "MsgType")
    protected String msgType;
    /**
     * 消息id，64位整型
     */
    @XmlElement(name = "MsgId")
    protected String msgId;
    /**
     * 消息的数据ID（消息如果来自文章时才有）
     */
    @XmlElement(name = "MsgDataId")
    protected String msgDataId;
    /**
     * 多图文时第几篇文章，从1开始（消息如果来自文章时才有）
     */
    @XmlElement(name = "Idx")
    protected String idx;
    /**
     * 消息创建时间 （整型）
     */
    @XmlElement(name = "CreateTime")
    protected long createTime;
    /**
     * 文本消息内容
     */
    @XmlElement(name = "Content")
    private String content;

}
