package com.idaymay.dzt.app.web;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import io.sentry.spring.autoconfigure.SentryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {SentryAutoConfiguration.class, DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class}
        , scanBasePackages = {"com.idaymay.dzt"})
public class DztApplication {

    public static void main(String[] args) {
        SpringApplication.run(DztApplication.class, args);
    }

}
