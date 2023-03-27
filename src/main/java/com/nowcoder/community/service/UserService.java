package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String path;
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
    public User findUserByEmail(String email) { return userMapper.selectByEmail(email); }

    public Map<String, Object> login(User user) {
        Map<String, Object> msgs = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            msgs.put("usernameMsg", "用户名不能为空");
            return msgs;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            msgs.put("passwordMsg", "用户密码不能为空");
            return msgs;
        }
        if (userMapper.selectByName(user.getUsername()) == null) {
            msgs.put("usernameMsg", "用户不存在");
        }
        return msgs;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> msgs = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            msgs.put("usernameMsg", "用户名不能为空");
            return msgs;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            msgs.put("passwordMsg", "用户密码不能为空");
            return msgs;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            msgs.put("emailMsg", "邮箱不能为空");
            return msgs;
        }
        //  验证用户名是否已使用
        if (userMapper.selectByName(user.getUsername()) != null) {
            msgs.put("usernameMsg", "用户名已存在");
            return msgs;
        }
        //  验证邮箱是否已使用
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            msgs.put("emailMsg", "邮箱已存在");
            return msgs;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);  //  未激活
        //  http://images.nowcoder.com/head/12t.png
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        // 访问激活服务url示例：http://localhost:8080/community/user/activation/100/code
        Context context = new Context();
        String url = String.format("%s%s/user/activation/%d/%s", domain, path, user.getId(), user.getActivationCode());
        context.setVariable("email", user.getEmail());
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.send(user.getEmail(), "community社区激活邮件", content);
        return msgs;
    }
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }
        if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }
        return ACTIVATION_ERROR;
    }
}
