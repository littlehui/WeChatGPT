package com.idaymay.dzt.common.ajax;

import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/7/27 14:57
 */
public class MediaTypeNoDeprecated {

    static {
        APPLICATION_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);
    }

    /**
     * spring 的这个常量过时了，某天可能就给删了。就放这里替代。
     */
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    public static final MediaType APPLICATION_JSON_UTF8;

}
