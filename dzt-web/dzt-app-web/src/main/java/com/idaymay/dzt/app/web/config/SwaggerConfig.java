package com.idaymay.dzt.app.web.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@ConditionalOnProperty(name = "show", prefix = "swagger", havingValue = "true", matchIfMissing = true)
@EnableKnife4j
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${swagger.show}")
    private boolean swaggerShow;

    @Bean
    public GroupedOpenApi defaultGrpuApi() {
        return GroupedOpenApi.builder()
                .group("接口")
                .pathsToMatch("/**")
                .packagesToScan("com.idaymay.dzt.app.web.controller")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API 接口文档")
                        .version("0.0.1")
                        .description("Knife4j + springdoc-openapi 集成示例\n" +
                                "返回成功：<br>" +
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
                        .contact(new Contact()
                                .name("开发者")
                                .email("[email protected]")))
                .components(new Components()
                        .addSecuritySchemes("token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")));
    }

}
