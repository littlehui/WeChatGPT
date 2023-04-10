package com.idaymay.dzt.app.web;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.idaymay.dzt.app.web.interceptor.RedissonLockAspect;
import org.checkerframework.checker.units.qual.C;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class}
		, scanBasePackages = {"com.idaymay.dzt"})
public class DztApplication {

	public static void main(String[] args) {
		SpringApplication.run(DztApplication.class, args);
	}

}
