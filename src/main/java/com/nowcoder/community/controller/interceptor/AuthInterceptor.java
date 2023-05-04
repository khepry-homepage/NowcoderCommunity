package com.nowcoder.community.controller.interceptor;


import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor {

    @Bean("AuthInterceptor")
    public HandlerInterceptor getAuthInterceptor() {
        return new SaInterceptor(handle -> {
                    // 根据路由划分模块，不同模块不同鉴权
                    SaRouter.match("/home/**",
                                    "/user/**",
                                    "/discuss/**",
                                    "/message/**",
                                    "/profile/**",
                                    "/follow/**",
                                    "/like/**",
                                    "/search")    // 拦截的 path 列表，可以写多个 */
                            .notMatch("/home/index", "/login", "/user/header/**")        // 排除掉的 path 列表，可以写多个
                            .check(r -> StpUtil.checkLogin())
                            .check(r -> StpUtil.checkRole("user"));        // 要执行的校验动作，可以写完整的 lambda 表达式
                    SaRouter.match("/discuss/setType",
                                    "/discuss/setStatus")
                            .check(r -> StpUtil.checkRole("moderator"));
                    SaRouter.match("/discuss/deletePost")
                            .check(r -> StpUtil.checkRole("admin"));
                });
    }
}
