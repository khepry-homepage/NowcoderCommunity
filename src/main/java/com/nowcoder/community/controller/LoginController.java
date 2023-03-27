package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login() {
        return "site/login";
    }
    @RequestMapping(path = "login", method = RequestMethod.POST)
    public String login(Model model, User user){
        Map<String, Object> msgs = userService.login(user);
        if (msgs == null || msgs.isEmpty()) {
            return "redirect:/home/index";
        }
        model.addAttribute("usernameMsg", msgs.get("usernameMsg"));
        model.addAttribute("passwordMsg", msgs.get("passwordMsg"));
        return "site/login";
    }
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String register() {
        return "site/register";
    }
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> msgs = userService.register(user);
        if (msgs == null || msgs.isEmpty()) {
            model.addAttribute("msg", "注册成功，已向您的邮箱发送一封激活邮件，请尽快点击激活。");
            model.addAttribute("target", "/home/index");
            return "site/operate-result";
        }
        model.addAttribute("usernameMsg", msgs.get("usernameMsg"));
        model.addAttribute("passwordMsg", msgs.get("passwordMsg"));
        model.addAttribute("emailMsg", msgs.get("emailMsg"));
        return "site/register";
    }
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_ERROR) {
            model.addAttribute("msg", "激活失败，返回登录页面。");
            model.addAttribute("target", "/home/index");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账户已经激活。");
            model.addAttribute("target", "/home/index");
        } else {
            model.addAttribute("msg", "激活成功");
            model.addAttribute("target", "/user/login");
        }
        return "site/operate-result";
    }
}
