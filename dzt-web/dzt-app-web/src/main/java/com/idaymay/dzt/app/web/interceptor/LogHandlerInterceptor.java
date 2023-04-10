package com.idaymay.dzt.app.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * <p>
 * 拦截器
 */
@Log4j2
public class LogHandlerInterceptor implements HandlerInterceptor {

    /**
     * 请求方法执行之前
     * 返回true则通过
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        StringBuffer requestURL = request.getRequestURL();
        log.info("请求URL：" + requestURL.toString());
        return true;
    }

    /**
     * 返回modelAndView之前执行
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * 执行Handler完成执行此方法
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
