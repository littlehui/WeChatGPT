package com.idaymay.dzt.common.ajax;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * <p>
 * 响应实体类封装
 *
 * @Author niujinpeng
 * @Date 2018/12/19 17:13
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    /**
     * 响应码
     */
    private Integer code = 200;
    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;


}
