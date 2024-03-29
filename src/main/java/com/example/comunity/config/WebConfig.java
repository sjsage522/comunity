package com.example.comunity.config;

import com.example.comunity.security.AuthCheckInterceptor;
import com.example.comunity.security.AuthUserHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor())
                .addPathPatterns("/**")
//                .excludePathPatterns("/api/download/*")
                .excludePathPatterns("/api/login")  //로그인은 요청을 인터셉트 안함
                .excludePathPatterns("/api/join"); //회원가입은 요청을 인터셉트 안함
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthUserHandlerMethodArgumentResolver());
    }

//    @Override
//    public void addCorsMappings(CorsRegistry cr) {
//        cr.addMapping("/**")
//                .allowedOrigins("http://localhost:8000");
//    }

    @Bean
    public AuthCheckInterceptor authCheckInterceptor() {
        return new AuthCheckInterceptor();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}