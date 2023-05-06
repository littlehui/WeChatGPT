package com.idaymay.dzt.app.web.controller.common;

import com.idaymay.dzt.app.web.DztApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @Description TODO
 * @ClassName BaseControllerTest
 * @Author littlehui
 * @Date 2021/7/7 13:24
 * @Version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DztApplication.class)
@WebAppConfiguration
@Slf4j
public class BaseControllerTest {


    @Before
    public void init() {
        log.info("开始测试...");
    }

    @After
    public void after() {
        log.info("测试结束...");
    }

}
