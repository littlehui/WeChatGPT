package com.idaymay.dzt.app.web.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;


@Configuration
@ConditionalOnProperty(name = "show", prefix = "swagger", havingValue = "true", matchIfMissing = true)
@EnableOpenApi
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${swagger.show}")
    private boolean swaggerShow;

    @Bean
    public Docket createRestApi() {
        List<RequestParameter> parameters = new ArrayList<>();
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(swaggerShow)
                .globalRequestParameters(parameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.idaymay.dzt.app.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("idaymay dtz 接口文档")
                //.termsOfServiceUrl("https://www.codingme.net")
                //.contact("niumoo")
                .version("0.0.1")
                .description("返回成功：<br>" +
                        "{<br>" +
                        "  \"code\": \"200\",<br>" +
                        "  \"message\": \"操作成功\",<br>" +
                        "  \"data\": {}<br>" +
                        "}<br>" +
                        "返回失败：<br>" +
                        "{<br>" +
                        "  \"code\": \"-1\",<br>" +
                        "  \"message\": \"未知异常\",<br>" +
                        "  \"data\": {}<br>" +
                        "}")
                .build();
    }
}
