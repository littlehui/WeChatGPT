package com.idaymay.dzt.service.command;

import java.util.List;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 11:31
 */
public interface Command<R extends CommandResult, T> {

    public R execute(T t);
}
