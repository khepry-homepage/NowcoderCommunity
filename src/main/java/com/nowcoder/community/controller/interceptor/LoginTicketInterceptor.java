package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.CookieUtil;
import com.nowcoder.community.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, Constants.LOGIN_TICKET);
        if (ticket != null) {
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
            //  Ticket有效
            if (loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.findUserById(loginTicket.getUserId());
                userHolder.set(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = userHolder.get();
        if (user != null  && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userHolder.remove();
    }
}
