package com.idaymay.dzt.service.impl;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

/**
 * 从 LLM 调用链异常中解析 HTTP 状态，用于对齐原 OpenAI 客户端的 400/401 分支语义。
 */
public final class LlmHttpStatusUtil {

    private LlmHttpStatusUtil() {
    }

    public static int resolveHttpStatus(Throwable t) {
        for (Throwable c = t; c != null; c = c.getCause()) {
            if (c instanceof HttpClientErrorException) {
                return ((HttpClientErrorException) c).getStatusCode().value();
            }
            if (c instanceof HttpServerErrorException) {
                return ((HttpServerErrorException) c).getStatusCode().value();
            }
            if (c instanceof RestClientResponseException) {
                return ((RestClientResponseException) c).getStatusCode().value();
            }
        }
        return -1;
    }
}
