package com.idaymay.dzt.app.web.controller;

import com.idaymay.dzt.bean.constant.DztCommandConstant;
import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.bean.param.WxTokenAuthParam;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.common.ajax.Response;
import com.idaymay.dzt.common.ajax.ResponseFactory;
import com.idaymay.dzt.common.constants.ApiVersionConstant;
import com.idaymay.dzt.common.exception.BusinessException;
import com.idaymay.dzt.common.swagger.ApiVersion;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.CheckService;
import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.service.command.Command;
import com.idaymay.dzt.service.command.CommandFactory;
import com.idaymay.dzt.service.command.CommandResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(value = "微信公众号", tags = "微信公众号")
@NoArgsConstructor
@Validated
@Slf4j
public class AppIndexController {

    @Autowired
    CheckService checkService;

    @Autowired
    ChatService chatService;

    @Autowired
    CommandFactory commandFactory;

    @GetMapping(value = "/wechat", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "微信公众号推送数据接口", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    @ResponseBody
    public String check(WxTokenAuthParam wxTokenAuthParam, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        return checkService.checkIndexSign(wxTokenAuthParam);
    }

    @PostMapping(value = "/wechat", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation(value = "微信公众号推送数据接口", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    @ResponseBody
    public Object message(@RequestBody WeChatMessage requestMessage, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        log.info("messge 接收到：{}", requestMessage);
        String content = requestMessage.getContent();
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
        if (content.startsWith(DztCommandConstant.COMMAND_PRE)) {
            //命令开头 setApiKey xxxxxx
            String[] args = content.split(" ");
            String commandName = args[0];
            List<String> commandArgs = new ArrayList<String>();
            for (int i = 0; i < args.length ; i++) {
                if (i>0) {
                    commandArgs.add(args[i]);
                }
            }
            Command command = commandFactory.createCommand(commandName);
            String argString = content.substring(content.indexOf(" "), content.length() - 1).trim();
            if (argString != null) {
                CommandResult commandResult = command.execute(fromUserName, commandArgs);
                responseMessage.setContent(commandResult.getMessage());
                return responseMessage;
            }
        }
        if (content.startsWith(ChatConstants.ANSWER_PRE)) {
            String messageId = content;
            String answer = chatService.answerAQuestion(messageId);
            responseMessage.setContent(answer);
        } else {
            responseMessage.setContent(chatService.askAQuestion(requestMessage.getContent(), fromUserName, toUserName));
        }
        return responseMessage;
    }

    @GetMapping(value = "/gpt", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "chatGpt提问接口", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    @ResponseBody
    public Response<String> gpt(QuestionDTO question, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        log.info("messge 接收到：{}", question);
        String answer = chatService.chat(question);
        return ResponseFactory.success(answer);
    }
}
