package com.idaymay.dzt.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
 * @date 2023/04/11 16:09
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema
public class QuestionDTO implements Serializable {

    private String messageId;

    private String question;

    private Long askTimeMills;

    private String userCode;

    private Integer requestTimes;
}
