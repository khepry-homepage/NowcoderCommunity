package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.MailClient;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    private Logger logger  = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;
    @Autowired
    private Producer captchaProducer;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login() {
        return "site/login";
    }
    @RequestMapping(path = "login", method = RequestMethod.POST)
    public String login(Model model, User user, String captcha, Boolean isRemember, HttpSession session, HttpServletResponse httpServletResponse){
        if (user == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (Strings.isBlank(captcha) || !captcha.equalsIgnoreCase((String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY))) {
            model.addAttribute("captchaMsg", "验证码错误");
            return "site/login";
        }
        int ticketDuration = isRemember ? Constants.TICKET_LONGER_DURATION : Constants.TICKET_DEFAULT_DURATION;
        Map<String, Object> msgs = userService.login(user.getUsername(), user.getPassword(), ticketDuration);
        if (msgs.containsKey("passwordMsg")) {
            model.addAttribute("passwordMsg", msgs.get("passwordMsg"));
            return "site/login";
        }
        if (msgs.containsKey("ticket")) {
            model.addAttribute("usernameMsg", msgs.get("usernameMsg"));
            model.addAttribute("passwordMsg", msgs.get("passwordMsg"));
        }
        Cookie cookie = new Cookie(Constants.LOGIN_TICKET, (String)msgs.get("ticket"));
        cookie.setPath(contextPath);
        cookie.setMaxAge(ticketDuration);
        httpServletResponse.addCookie(cookie);
        return "redirect:/home/index";
    }
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String forget() {
        return "site/forget";
    }
    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    public String resetPassword(Model model, @CookieValue(Constants.LOGIN_TICKET) String login_ticket, String email, String captcha, String newPassword, HttpSession session) {
        if (email == null) {
            model.addAttribute("emailMsg", "邮箱不能为空");
            return "site/forget";
        }
        if (newPassword == null) {
            model.addAttribute("newPasswordMsg", "重置密码不能为空");
            return "site/forget";
        }
        if (Strings.isBlank(captcha) || !captcha.equalsIgnoreCase((String)session.getAttribute(Constants.RESET_PASSWORD_KAPTCHA_SESSION_KEY))) {
            model.addAttribute("captchaMsg", "验证码无效");
            return "site/forget";
        }
        Map<String, Object> msgs = userService.resetPassword(email, newPassword);
        if (msgs != null && !msgs.isEmpty()) {
            model.addAttribute("emailMsg", msgs.get("emailMsg"));
            return "site/forget";
        }
        session.removeAttribute(Constants.RESET_PASSWORD_KAPTCHA_SESSION_KEY);
        if (login_ticket != null) {
            userService.logout(login_ticket);
        }
        model.addAttribute("msg", "重置密码成功");
        model.addAttribute("target", "/login");
        return "site/operate-result";
    }
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue(Constants.LOGIN_TICKET) String login_ticket) {
        userService.logout(login_ticket);
        return "redirect:/login";
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
        if (result == Constants.ACTIVATION_ERROR) {
            model.addAttribute("msg", "激活失败，返回登录页面。");
            model.addAttribute("target", "/home/index");
        } else if (result == Constants.ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账户已经激活。");
            model.addAttribute("target", "/home/index");
        } else {
            model.addAttribute("msg", "激活成功");
            model.addAttribute("target", "/login");
        }
        return "site/operate-result";
    }
    @RequestMapping(path = "/getCaptcha", method = RequestMethod.GET)
    public void getCaptcha(HttpServletResponse httpServletResponse, HttpSession session) {
        //  生成验证码并写入响应报文
        httpServletResponse.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = captchaProducer.createImage(capText);
        try {
            ServletOutputStream out = httpServletResponse.getOutputStream();
            ImageIO.write(bi, "jpg", out);
        }catch (Exception e){
            logger.error("生成验证码失败", e.getMessage());
        }
    }
    @RequestMapping(path = "/sendCaptcha", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sendCaptcha(@RequestParam("email") String email, HttpSession session) {
        Map<String, Object> msgs = new HashMap<>();
        if (email == null) {
            msgs.put("emailMsg", "邮箱不能为空！");
            return msgs;
        }
        session.setMaxInactiveInterval(Constants.RESET_PASSWORD_CAPTCHA_DURATION);
        String captcha = captchaProducer.createText();
        Context context = new Context();
        session.setAttribute(Constants.RESET_PASSWORD_KAPTCHA_SESSION_KEY, captcha);
        context.setVariable("email", email);
        context.setVariable("captcha", captcha);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.send(email, "重置密码", content);
        msgs.put("emailMsg", "验证码已发送, 有效时间5分钟");

        return msgs;
    }
}
