package com.idaymay.dzt.dao.redis.domain;

import com.idaymay.dzt.dao.redis.optype.SortScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/16 00:34
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageCache implements SortScore {

    private static Long timeMillsBend = 599587199000L;

    private String role;

    private String content;

    private String name;

    private Long createTimeMills;

    private Double score;

    @Override
    public double getScore() {
        return createTimeMills - timeMillsBend;
    }
}
