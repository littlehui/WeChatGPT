package com.idaymay.dzt.common.servlet;

import javax.servlet.http.HttpServletRequest;

public class WebContext {

    private static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<String> userThreadLocal = new ThreadLocal<>();

    public static void setRequest(HttpServletRequest httpServletRequest) {
        requestThreadLocal.set(httpServletRequest);
    }

    public static void setUserCode(String userCode) {
        userThreadLocal.set(userCode);
    }

    public static String getUserCode() {
        return userThreadLocal.get();
    }
}
