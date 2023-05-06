package com.idaymay.dzt.app.web.service;

import com.idaymay.dzt.app.web.service.common.BaseTestService;
import com.idaymay.dzt.service.OpenAiService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
public class OpenAiServiceTests extends BaseTestService {

    @Autowired
    OpenAiService openAiService;

    @Test
    public void chatTest() {
        System.out.println(openAiService.chat("2022世界杯冠军是哪只球队？"));
    }
}
