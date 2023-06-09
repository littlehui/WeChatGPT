package com.idaymay.dzt.app.web.handler;

import com.idaymay.dzt.app.web.utils.WebUtil;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.common.ajax.Response;
import com.idaymay.dzt.common.ajax.ResponseEnum;
import com.idaymay.dzt.common.ajax.ResponseFactory;
import com.idaymay.dzt.common.exception.*;
import com.idaymay.dzt.common.utils.obj.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;


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

    @ResponseBody
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<String> handleRateLimitException(RateLimitException e, HttpServletRequest request, HttpServletResponse response) {
        WeChatMessage weChatMessage = e.getWeChatMessage();
        XmlRootElement xmlRootElement = WeChatMessage.class.getAnnotation(XmlRootElement.class);
        if (xmlRootElement != null) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(WeChatMessage.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(weChatMessage, stringWriter);
                String xml = stringWriter.toString();
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);
            } catch (JAXBException ex) {
                log.error("jaxb exception", ex);
            }
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(GsonUtil.toJson(weChatMessage));
        }
        return ResponseEntity.ok("");
    }


    @ResponseBody
    @ExceptionHandler(AnswerTimeOutException.class)
    public ResponseEntity<String> handleAnswerTimeOutException(AnswerTimeOutException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn("回答时间超时。messageId:{},userCode:{},超时次数:{}", e.getMessageId(), e.getUserCode(), e.getTimeOutCount());
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
    }

    private String getRequestContentType() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getContentType();
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
