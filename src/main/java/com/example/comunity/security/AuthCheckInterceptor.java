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

public class AuthCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler) {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth authOnMethod = handlerMethod.getMethodAnnotation(Auth.class);
        if (authOnMethod == null) return true;

        HttpSession session = request.getSession();
        if (session == null) {
            throw new IllegalStateException("HttpSession is not created");
        }

        User authUser = (User) session.getAttribute("authInfo");
        if (authUser == null) {
            throw new NotLoginUserException(ErrorCode.NOT_LOGIN_USER);
        }

        Auth authOnClass = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Auth.class);
        if (authOnClass != null) {
            String role = authOnClass.role().toString();
            if ("ADMIN".equals(role)) {
                if (!"admin".equals(authUser.getUserId())) {
                    throw new AccessDeniedException();
                }
            }
        }

        return true;
    }
}
