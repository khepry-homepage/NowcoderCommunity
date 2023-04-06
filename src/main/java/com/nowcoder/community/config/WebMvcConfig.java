package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//  代理类DelegatingWebMvcConfiguration负责注入所有实现了WebMvcConfigurer的配置类
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;      //  自定义拦截器
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;  //  用户可用功能鉴权

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/.css", "/**/.js", "/**/.jpg", "/**/.png", "/**/.jpeg");
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/.css", "/**/.js", "/**/.jpg", "/**/.png", "/**/.jpeg");
    }
}
