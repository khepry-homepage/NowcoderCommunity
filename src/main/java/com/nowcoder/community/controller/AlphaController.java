package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping(path = "/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping("/login")
    @ResponseBody
    public String login() {
        return alphaService.getUserInfo();
    }
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    @ResponseBody
    public boolean register(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "password", required = true) String password) {
        Set<String> usernames = new HashSet<String>();
        usernames.add("root");
        Set<String> passwords = new HashSet<String>();
        passwords.add("123456");
        return usernames.contains(username) && passwords.contains(password);
    }
    @RequestMapping(path = "/getUserInfo", method = RequestMethod.GET)
    public ModelAndView getUserInfo() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("username", "root");
        mv.addObject("password", "123456");
        mv.setViewName("demo/userInfo");
        return mv;
    }
    @RequestMapping(path = "/getAvatar", method = RequestMethod.GET)
    public Map<String, Object> getAvatar() {
        Map<String, Object> jsonResult = new HashMap<>();
        jsonResult.put("username", "root");
        jsonResult.put("password", "123456");
        jsonResult.put("type", 0);
        return jsonResult;
    }
    @RequestMapping(path = "/setCookie", method = RequestMethod.GET)
    public String setCookie(HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie("name", "test");
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60 * 10);
        httpServletResponse.addCookie(cookie);
        return "Set Cookie";
    }
    @RequestMapping(path = "/getCookie", method = RequestMethod.GET)
    public String getCookie(@CookieValue("name") String name) {
        System.out.println(name);
        return "Get Cookie";
    }
    @RequestMapping(path = "/setSession", method = RequestMethod.GET)
    public String setSession(HttpSession session) {
        session.setAttribute("number", new Random().nextInt(1000));
        return "Set Session";
    }
    @RequestMapping(path = "/getSession", method = RequestMethod.GET)
    public String getSession(HttpSession session) { //  此处session是cookie到session的映射，如HttpSession session = request.getSession()
        System.out.println(session.getAttribute("number"));
        return "Get Session";
    }
}
