package com.nowcoder.community.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getValue(HttpServletRequest httpServletRequest, String name) {
        if (httpServletRequest == null || name == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
