package com.example.comunity.security;

import com.example.comunity.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Component
@Slf4j
public class AuthUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(AuthUser.class) != null) &&
                parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = Optional.ofNullable(webRequest.getNativeRequest(HttpServletRequest.class))
                .orElseThrow(IllegalStateException::new);
        HttpSession session = request.getSession();

        User authInfo = (User) session.getAttribute("authInfo");
        log.info("[AuthUserHandlerMethodArgumentResolver] authInfo = {}", authInfo);
        return authInfo;
    }
}
