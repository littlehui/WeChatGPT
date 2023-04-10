package com.idaymay.dzt.app.web.config;


import com.google.gson.Gson;
import com.idaymay.dzt.app.web.interceptor.LogHandlerInterceptor;
import com.idaymay.dzt.common.mvc.ConverterDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 1.使用FastJSON
 * 2.配置时间格式化
 * 3.解决中文乱码
 * 4.添加自定义拦截器
 * 5.时间转换器
 *
 * @Author niujinpeng
 * @Date 2018/12/13 15:35
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 自定义JSON转换器
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        //处理中文乱码问题
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        Gson gson = new Gson().newBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").excludeFieldsWithoutExposeAnnotation().create();
        gsonHttpMessageConverter.setGson(gson);
        gsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        converters.add(gsonHttpMessageConverter);
    }

    /**
     * 添加自定义拦截器
     * .addPathPatterns("/**")  拦截的请求路径
     * .excludePathPatterns("/user"); 排除的请求路径
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/index");
    }

    /**
     * 时间转换器
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ConverterDate());
    }

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                HttpHeaders headers = httpRequest.getHeaders();
                headers.set("Content-type", "application/json;charset=utf-8");
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        });
        return restTemplate;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOriginPatterns("*.1y.com", "*.1y.com:*")
                // 设置允许请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                // 是否允许证书（cookies）
                .allowCredentials(true)
                // 预请求的结果能被缓存多久
                .maxAge(3600)
                // 设置允许的请求头
                .allowedHeaders("game-code", "terminal", "did", "content-type");
    }
}