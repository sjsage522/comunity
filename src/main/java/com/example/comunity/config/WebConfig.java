package com.example.comunity.config;

import com.example.comunity.interceptor.AuthCheckInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .excludePathPatterns("/login")  //로그인은 요청을 인터셉트 안함
                .excludePathPatterns("/users"); //회원가입은 요청을 인터셉트 안함
    }

    @Bean
    public AuthCheckInterceptor authCheckInterceptor() {
        return new AuthCheckInterceptor();
    }
}
