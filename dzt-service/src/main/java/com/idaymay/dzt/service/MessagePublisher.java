package com.idaymay.dzt.service;

import com.idaymay.dzt.service.constant.MessageConstant;

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
