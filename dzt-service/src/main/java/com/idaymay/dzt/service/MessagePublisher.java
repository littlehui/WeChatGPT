package com.idaymay.dzt.service;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:45
 */
public interface MessagePublisher {

    public <T> void publishMessage(T message);

}
