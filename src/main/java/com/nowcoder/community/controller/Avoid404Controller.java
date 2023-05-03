package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Deprecated
@Controller
public class Avoid404Controller {
    /**
     * Spring MVC底层机制引起的坑：
     * 如果请求接口不存在，并且在拦截器里抛出异常的话该异常不会进入全局处理。
     * 因此用@RequestMapping("/**") 的路由专门处理404，避免匹配不到请求的接口从而绕过全局异常处理
     * https://github.com/dromara/Sa-Tokn/issues/166
     */
//    @RequestMapping("/**")
//    public void error404Dispose() {
//
//    }
}
