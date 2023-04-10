package com.idaymay.dzt.common.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description TODO
 * @ClassName ApiVersion
 * @Author littlehui
 * @Date 2021/10/20 15:33
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiVersion {

    /**
     * 接口版本号(对应swagger中的group)
     * @return String[]
     */
    String[] group();

}
