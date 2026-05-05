package com.idaymay.dzt.app.web.controller.common;

import com.idaymay.dzt.app.web.DztApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @Description TODO
 * @ClassName BaseControllerTest
 * @Author littlehui
 * @Date 2021/7/7 13:24
 * @Version 1.0
 **/
@SpringBootTest(classes = DztApplication.class)
@WebAppConfiguration
@Slf4j
public class BaseControllerTest {

    @BeforeEach
    public void init() {
        log.info("开始测试...");
    }

    @AfterEach
    public void after() {
        log.info("测试结束...");
    }

}
