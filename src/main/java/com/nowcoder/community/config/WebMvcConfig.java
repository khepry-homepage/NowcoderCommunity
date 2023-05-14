package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.AuthInterceptor;
import com.nowcoder.community.controller.interceptor.RouteInterceptor;
import com.nowcoder.community.controller.interceptor.StatisticsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//  代理类DelegatingWebMvcConfiguration负责注入所有实现了WebMvcConfigurer的配置类
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    @Qualifier("AuthInterceptor")
    private HandlerInterceptor authInterceptor;
    @Autowired
    private RouteInterceptor routeInterceptor;
    @Autowired
    private StatisticsInterceptor statisticsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpeg");
        registry.addInterceptor(routeInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpeg");
        registry.addInterceptor(statisticsInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpeg");
    }
}
