package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.Annotation.LoginRequired;
import com.nowcoder.community.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private UserHolder userHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            if (method.getAnnotation(LoginRequired.class) != null && userHolder.get() == null) {
                response.sendRedirect(request.getContextPath() + "/login"); //  重定向到登录页面
                return false;   //  不再处理该请求
            }
        }
        return true;
    }
}
