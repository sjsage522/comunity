package com.example.comunity.config;

import com.example.comunity.interceptor.AuthCheckInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor())
                .addPathPatterns("/*")
                .addPathPatterns("/*/*")
                .addPathPatterns("/*/*/*")
                .addPathPatterns("/*/*/*/*")
                .excludePathPatterns("/login")  //로그인은 요청을 인터셉트 안함
                .excludePathPatterns("/users"); //회원가입은 요청을 인터셉트 안함
    }

    @Bean
    public AuthCheckInterceptor authCheckInterceptor() {
        return new AuthCheckInterceptor();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");                  //파일 인코딩 설정
        multipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024);     //파일 업로드 크기제한 설정 (5MB)
        return multipartResolver;
    }
}
