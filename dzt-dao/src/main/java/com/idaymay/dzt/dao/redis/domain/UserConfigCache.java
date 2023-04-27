package com.idaymay.dzt.dao.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/23 17:56
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConfigCache implements Serializable {

    private String userCode;

    private String openAiApiKey;

}
