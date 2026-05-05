package com.idaymay.dzt.app.web.controller;

import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.bean.param.WxTokenAuthParam;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.common.ajax.Response;
import com.idaymay.dzt.common.ajax.ResponseFactory;
import com.idaymay.dzt.common.constants.ApiVersionConstant;
import com.idaymay.dzt.common.exception.BusinessException;
import com.idaymay.dzt.common.swagger.ApiVersion;
import com.idaymay.dzt.dao.redis.repository.AnswerCacheRepository;
import com.idaymay.dzt.dao.redis.repository.CurrentQuestionCheckRepository;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.CheckService;
import com.idaymay.dzt.service.impl.WxChatStrageyImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "微信公众号", description = "微信公众号")
@NoArgsConstructor
@Validated
@Slf4j
public class AppIndexController {

    @Autowired
    CheckService checkService;

    @Autowired
    ChatService chatService;

    @Autowired
    WxChatStrageyImpl wxChatStragey;

    @GetMapping(value = "/wechat", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(description = "微信公众号推送数据接口", summary = "微信公众号推送数据接口")
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    @ResponseBody
    public String check(@ParameterObject WxTokenAuthParam wxTokenAuthParam) throws BusinessException {
        return checkService.checkIndexSign(wxTokenAuthParam);
    }

    @PostMapping(value = "/wechat", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(description = "微信公众号推送数据接口", summary = "微信公众号推送数据接口")
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    @ResponseBody
    public Object message(@RequestBody WeChatMessage requestMessage) throws BusinessException {
        return wxChatStragey.receiveAndChat(requestMessage.getMsgId(), requestMessage);
    }

    @GetMapping(value = "/gpt", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "chatGpt提问接口", summary = "chatGpt提问接口")
    @ApiVersion(group = ApiVersionConstant.FAP_APP010)
    @ResponseBody
    public Response<String> gpt(@ParameterObject QuestionDTO question, HttpServletRequest httpServletRequest, HttpServletResponse response) throws BusinessException {
        log.info("messge 接收到：{}", question);
        String answer = chatService.chat(question.getMessageId(), question);
        return ResponseFactory.success(answer);
    }
}