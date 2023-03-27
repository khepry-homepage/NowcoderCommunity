package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//@RestController
//@RequestMapping(path = "/user")
//public class AlphaController {
//    @Autowired
//    private AlphaService alphaService;
//    @RequestMapping("/login")
//    @ResponseBody
//    public String login() {
//        return alphaService.getUserInfo();
//    }
//    @RequestMapping(path = "/register", method = RequestMethod.POST)
//    @ResponseBody
//    public boolean register(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "password", required = true) String password) {
//        Set<String> usernames = new HashSet<String>();
//        usernames.add("root");
//        Set<String> passwords = new HashSet<String>();
//        passwords.add("123456");
//        return usernames.contains(username) && passwords.contains(password);
//    }
//    @RequestMapping(path = "/getUserInfo", method = RequestMethod.GET)
//    public ModelAndView getUserInfo() {
//        ModelAndView mv = new ModelAndView();
//        mv.addObject("username", "root");
//        mv.addObject("password", "123456");
//        mv.setViewName("demo/userInfo");
//        return mv;
//    }
//    @RequestMapping(path = "/getAvatar", method = RequestMethod.GET)
//    public Map<String, Object> getAvatar() {
//        Map<String, Object> jsonResult = new HashMap<>();
//        jsonResult.put("username", "root");
//        jsonResult.put("password", "123456");
//        jsonResult.put("type", 0);
//        return jsonResult;
//    }
//}
