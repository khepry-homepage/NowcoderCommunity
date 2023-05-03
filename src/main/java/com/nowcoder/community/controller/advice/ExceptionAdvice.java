package com.nowcoder.community.controller.advice;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import com.nowcoder.community.utils.CommunityUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public SaResult doNotLoginException(NotLoginException ex) {
        SaRequest request = SaHolder.getRequest();
        SaResponse response = SaHolder.getResponse();
        String xRequestedWith = request.getHeader("x-requested-with");
        //  响应数据需要为JSON格式
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setHeader("Content-Type", "application/plain;charset=utf-8");
            return SaResult.get(403, ex.getMessage(), "未登录");
        }
        response.redirect( contextPath + "/login");
        return SaResult.get(403, ex.getMessage(), "未登录");
    }
}
