package com.idaymay.dzt.common.ajax;

import java.util.ArrayList;

/**
 * 常用Response工厂.
 * @Company : cyou
 * @author littlehui
 * @date   2012-9-29 下午02:29:23
 */
public class ResponseFactory {

	public static <T> Response<T> success(T data) {
		return new Response<T>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), data);
	}

	public static Response success() {
		return success(new ArrayList<>());
	}

	public static Response result(Integer code, String message) {
		return new Response(code, message, new ArrayList<>());
	}

	public static <T> Response<T> result(ResponseEnum responseEnum) {
		return result(responseEnum.getCode(), responseEnum.getMessage());
	}

	public static <T> Response<T> result(ResponseEnum responseEnum, String message) {
		return result(responseEnum.getCode(), message);
	}

}
