package com.idaymay.dzt.app.web.service.common;

import com.idaymay.dzt.app.web.DztApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description TODO
 * @ClassName BaseTestService
 * @Author littlehui
 * @Date 2020/12/30 17:56
 * @Version 1.0
 **/
@SpringBootTest(classes = DztApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootConfiguration
//@ContextConfiguration(classes = PayServiceCoreApplication.class)
@Slf4j
public class BaseTestService {

}
