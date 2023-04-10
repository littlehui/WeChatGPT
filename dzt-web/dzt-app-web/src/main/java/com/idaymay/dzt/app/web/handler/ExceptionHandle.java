package com.idaymay.dzt.app.web.handler;

import com.idaymay.dzt.app.web.utils.WebUtil;
import com.idaymay.dzt.common.ajax.Response;
import com.idaymay.dzt.common.ajax.ResponseEnum;
import com.idaymay.dzt.common.ajax.ResponseFactory;
import com.idaymay.dzt.common.exception.BusinessException;
import com.idaymay.dzt.common.exception.ParamException;
import com.idaymay.dzt.common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;


/**
 * <p>
 * 统一的异常处理
 *
 * @Author niujinpeng
 * @Date 2019/1/7 14:26
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        printErrorDetail(e, request);
        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            Integer code = exception.getCode();
            String message = exception.getMessage();
            return ResponseFactory.result(code, message);
        }
        if (e.getCause() != null && e.getCause() instanceof ParamException) {
            ParamException exception = (ParamException) e.getCause();
            Integer code = exception.getCode();
            String message = exception.getMessage();
            return ResponseFactory.result(code, message);
        }
        if (e.getCause() != null && e.getCause() instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e.getCause();
            Integer code = businessException.getCode();
            String message = businessException.getMessage();
            return ResponseFactory.result(code, message);
        }
        if (e instanceof ConstraintViolationException) {
            return ResponseFactory.result(ResponseEnum.PARAM_INVALID);
        }
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException argumentNotValidException = (MethodArgumentNotValidException) e;
            if (argumentNotValidException.getBindingResult() != null
                    && argumentNotValidException.getBindingResult().getFieldError() != null) {
                return ResponseFactory.result(ResponseEnum.PARAM_INVALID.getCode(),
                        argumentNotValidException.getBindingResult().getFieldError().getDefaultMessage());
            } else {
                return ResponseFactory.result(ResponseEnum.PARAM_INVALID.getCode(), argumentNotValidException.getBindingResult().getFieldError().getField() + "参数异常");
            }
        }
        if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            if (bindException.getBindingResult() != null
                    && bindException.getBindingResult().getFieldError() != null) {
                return ResponseFactory.result(ResponseEnum.PARAM_INVALID.getCode(),
                        bindException.getBindingResult().getFieldError().getDefaultMessage());
            }
        }
        if (e instanceof SystemException) {
            SystemException exception = (SystemException) e;
            Integer code = exception.getCode();
            String message = exception.getMessage();
            return ResponseFactory.result(code, message);
        }
        if (e instanceof ParamException) {
            ParamException exception = (ParamException) e;
            Integer code = exception.getCode();
            String message = exception.getMessage();
            return ResponseFactory.result(code, message);
        }
        return ResponseFactory.result(ResponseEnum.UNKNOW_ERROR);
    }

    private void printErrorDetail(Exception e, HttpServletRequest request) {
        StringBuffer errorStr = new StringBuffer();
        errorStr.append("请求异常" + e).append("\n")
                .append("Aspect-URL: ").append(request.getRequestURI().toLowerCase()).append("\n")
                .append("Aspect-HTTP_METHOD: ").append(request.getMethod()).append("\n")
                .append("Aspect-IP: ").append(WebUtil.getIpAddress(request));
        log.error(errorStr.toString());
    }


    /**
     * 判断是否是Ajax请求
     *
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        if (null == request) {
            return false;
        }
        String rquested = request.getHeader("X-Rquested-With");
        if (!"XMLHttpRequest".equals(rquested)) {
            return false;
        }
        return true;
    }
}
