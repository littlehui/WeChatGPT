package com.idaymay.dzt.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 14:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandResult {

    private String message;
}
