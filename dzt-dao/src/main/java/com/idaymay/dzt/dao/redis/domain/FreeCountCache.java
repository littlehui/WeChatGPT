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
 * @date 2023/04/26 16:48
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeCountCache implements Serializable {

    private Integer freeCount;

    private String userCode;

}
