package com.idaymay.dzt.app.web.controller.common;

import com.idaymay.dzt.common.utils.obj.ObjectUtil;
import com.idaymay.dzt.common.utils.string.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

/**
 * @Description TODO
 * @ClassName ReaderControllerTest
 * @Author littlehui
 * @Date 2021/7/8 20:57
 * @Version 1.0
 **/
@Slf4j
public class DztAppControllerTest extends BaseControllerTest {

    private MockMvc mockMvc;

    private String accessToken;

    private String headerGameCode;

    private String terminal = "PC";

    public void setup(Object singleTon) {
        mockMvc = MockMvcBuilders.standaloneSetup(singleTon).build();
    }

    protected void setAuth(String accessToken) {
        this.accessToken = accessToken;
    }

    protected void setHeaderGame(String gameCode) {
        this.headerGameCode = gameCode;
    }

    protected void setHeaderTerminal(String terminal) {
        this.terminal = terminal;
    }

    public void post(Object param, String url) throws Exception {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("access_token", accessToken);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .cookie(cookie)
                        //.header("game-code", headerGameCode)
                        //.header("terminal", terminal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GsonUtil.toJson(param)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        log.info(mvcResult.getResponse().getContentAsString());
    }


    public void get(Object param, String url) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);
        requestBuilder.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        if (param != null) {
            //requestBuilder.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            if (param instanceof Map) {
                for (Object key : ((Map) param).keySet()) {
                    String value = ((Map) param).get(key) + "";
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        requestBuilder.param(key + "", ((Map) param).get(key) + "");
                    }
                }
            } else {
                Map<String, Object> paramMap = ObjectUtil.toMap(param);
                for (String key : paramMap.keySet()) {
                    String value = paramMap.get(key) + "";
                    if (value != null && !"".equals(value) && !"null".equals(value)) {
                        requestBuilder.param(key, paramMap.get(key) + "");
                    }
                }
            }
        }
        if (accessToken != null) {
            //requestBuilder.header("cookie", "access_token=" + accessToken);
            requestBuilder.header("game-code", headerGameCode).header("terminal", terminal);
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("access_token", accessToken);
            requestBuilder.cookie(cookie);
        }
        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        log.info(mvcResult.getResponse().getContentAsString());
    }

    public void delete(Object param, String url) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url);
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        if (param != null) {
            requestBuilder.content(GsonUtil.toJson(param));
        }
        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        log.info(mvcResult.getResponse().getContentAsString());
    }
}
