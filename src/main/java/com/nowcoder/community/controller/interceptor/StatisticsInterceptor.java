package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.StatisticsService;
import com.nowcoder.community.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class StatisticsInterceptor implements HandlerInterceptor {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private UserHolder userHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteHost();
        statisticsService.addUV(ip);
        User user = userHolder.get();
        if (user != null) {
            statisticsService.addDAU(user.getId());
        }
        return true;
    }
}
