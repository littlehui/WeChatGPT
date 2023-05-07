package com.idaymay.dzt.service.command;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 15:01
 */
public abstract class AbstractCommand<R extends CommandResult, T> implements Command<R, T> {

    protected DztCommandExecutor executor;

   public AbstractCommand(DztCommandExecutor executor) {
       this.executor = executor;
   }

}
