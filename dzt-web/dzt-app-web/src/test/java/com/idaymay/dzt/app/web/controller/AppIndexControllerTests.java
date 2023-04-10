package com.idaymay.dzt.app.web.controller;

import com.idaymay.dzt.app.web.controller.common.DztAppContextControllerAwareTest;
import com.idaymay.dzt.bean.param.WxTokenAuthParam;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/8/6 11:56
 */
@ActiveProfiles("local")
public class AppIndexControllerTests extends DztAppContextControllerAwareTest {

    @Before
    public void beforeTest() {

    }

    @Test
    public void index() throws Exception {
        WxTokenAuthParam wxTokenAuthParam = WxTokenAuthParam.builder()
                .echostr("5952339343081447358")
                .nonce("1214142239")
                .timestamp("1681033102")
                .signature("d3d449072c8cde6d1b6ee738e004f86eabe28500")
                .build();
        get(wxTokenAuthParam, "/");
    }
}
