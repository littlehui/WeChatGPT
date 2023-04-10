package com.idaymay.dzt.app.web.controller;

import com.idaymay.dzt.bean.param.WxTokenAuthParam;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.common.ajax.Response;
import com.idaymay.dzt.common.ajax.ResponseFactory;
import com.idaymay.dzt.common.constants.ApiVersionConstant;
import com.idaymay.dzt.common.exception.BusinessException;
import com.idaymay.dzt.common.swagger.ApiVersion;
import com.idaymay.dzt.service.IndexService;
import com.idaymay.dzt.service.OpenAiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "微信公众号", tags = "微信公众号")
@NoArgsConstructor
@Validated
@Slf4j
public class AppIndexController {

    @Autowired
    IndexService indexService;

    @Autowired
    OpenAiService openAiService;

    @GetMapping(value = "/wechat", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "微信公众号推送数据接口", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    public String check(WxTokenAuthParam wxTokenAuthParam, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        return indexService.checkIndexSign(wxTokenAuthParam);
    }

    @PostMapping(value = "/wechat", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation(value = "微信公众号推送数据接口", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    public Object message(@RequestBody WeChatMessage requestMessage, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        log.info("messge 接收到：{}", requestMessage);
        String fromUserName = requestMessage.getFromUserName();
        String toUserName = requestMessage.getToUserName();
        //新建一个响应对象
        WeChatMessage responseMessage = new WeChatMessage();
        //消息来自谁
        responseMessage.setFromUserName(toUserName);
        //消息发送给谁
        responseMessage.setToUserName(fromUserName);
        //消息类型，返回的是文本
        responseMessage.setMsgType("text");
        //消息创建时间，当前时间就可以
        responseMessage.setCreateTime(System.currentTimeMillis());
        //这个是响应消息内容，直接复制收到的内容做演示，甚至整个响应对象都可以直接使用原请求参数对象，只需要换下from和to就可以了哈哈哈
        responseMessage.setContent("success !");
        return responseMessage;
    }

    @GetMapping(value = "/gpt", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "chatGpt提问接口", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    public Response<String> gpt(String question, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        log.info("messge 接收到：{}", question);
        String answer = openAiService.chat(question);
        return ResponseFactory.success(answer);
    }
}
