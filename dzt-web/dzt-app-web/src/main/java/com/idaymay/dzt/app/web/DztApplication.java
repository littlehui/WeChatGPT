package com.idaymay.dzt.app.web;

import io.sentry.spring.autoconfigure.SentryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {SentryAutoConfiguration.class}
        , scanBasePackages = {"com.idaymay.dzt"})
public class DztApplication {

    public static void main(String[] args) {
        SpringApplication.run(DztApplication.class, args);
    }

}
