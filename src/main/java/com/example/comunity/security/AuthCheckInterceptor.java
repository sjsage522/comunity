package com.example.comunity.security;

import com.example.comunity.domain.User;
import com.example.comunity.dto.api.ErrorCode;
import com.example.comunity.exception.AccessDeniedException;
import com.example.comunity.exception.NotLoginUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static com.example.comunity.security.Auth.*;
import static com.example.comunity.security.Auth.Role.*;

@Slf4j
public class AuthCheckInterceptor implements HandlerInterceptor {

    private static final String PREFIX = "AuthCheckInterceptor";

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) {
        log.info("{} : preHandle(...) call", PREFIX);

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
            Role role = authOnClass.role();
            if (ADMIN.equals(role) && !"admin".equals(authUser.getUserId())) {
                throw new AccessDeniedException();
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("{} : postHandle(...) call", PREFIX);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("{} : afterCompletion(...) call", PREFIX);
    }
}
