package com.example.comunity.interceptor;

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
        HttpSession session = request.getSession();
        if (session != null) {
            Object authInfo = session.getAttribute("authInfo");
            return authInfo != null;
        }
        return false;
    }
}
