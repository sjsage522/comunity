package com.example.comunity.security;

import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ErrorCode;
import com.example.comunity.exception.AccessDeniedException;
import com.example.comunity.exception.NotLoginUserException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class AuthCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) {

        HttpSession session = Optional.ofNullable(request.getSession())
                .orElseThrow(() -> new IllegalStateException("HttpSession is not created"));

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth authOnMethod = handlerMethod.getMethodAnnotation(Auth.class);
        Auth authOnClass = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Auth.class);
        //메서드 레벨에 Auth 애노테이션이 없는 경우 (인증이 필요없는 요청)
        if (authOnMethod == null && authOnClass == null) return true;

        //세션에 사용자 정보가 저장되어 있는지 확인
        User authUser = Optional.ofNullable(
                (User) session.getAttribute("authInfo")
        )
                .orElseThrow(() -> new NotLoginUserException(ErrorCode.NOT_LOGIN_USER));

        //클래스 레벨에 붙어 있는 Auth 애노테이션 확인 (관리자 인증)
        if (authOnClass != null) {
            String role = authOnClass.role().toString();
            if ("ADMIN".equals(role) && !"admin".equals(authUser.getUserId())) {
                throw new AccessDeniedException();
            }
        }

        return true;
    }
}
