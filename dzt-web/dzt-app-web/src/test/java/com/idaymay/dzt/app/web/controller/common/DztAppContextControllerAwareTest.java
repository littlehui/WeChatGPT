package com.idaymay.dzt.app.web.controller.common;

import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description TODO
 * @ClassName ReaderContextControllerAwareTest
 * @Author littlehui
 * @Date 2021/7/10 17:23
 * @Version 1.0
 **/
public class DztAppContextControllerAwareTest extends DztAppControllerTest implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private static final String SUBFIX_TEST = "Tests";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Before
    public void setup() {
        String actualBeanName = this.getClass().getSimpleName();
        actualBeanName = toLowerCaseFirstOne(actualBeanName);
        Object controller = applicationContext.getBean(actualBeanName.replaceAll(SUBFIX_TEST, ""));
        super.setup(controller);
    }

    public String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //首字母转大写
    public String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
