package com.nowcoder.community.controller.advice;

import com.nowcoder.community.utils.CommunityUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler({Exception.class})
    public void handleControllerException(Exception e, HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {
        logger.error("服务器异常: ", e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        String xRequestedWith = request.getHeader("x-requested-with");
        //  响应数据需要为JSON格式
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(CommunityUtil.toJSONObject(500, "服务器异常"));
        } else {
            response.sendRedirect(request.getContextPath() + "/home/error");
        }
    }
}
