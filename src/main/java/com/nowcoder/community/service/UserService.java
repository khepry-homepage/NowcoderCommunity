package com.nowcoder.community.service;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.MailClient;
import com.nowcoder.community.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String path;
    public User findUserById(int id) {
        User user = getUserFromCache(id);
        if (user == null) {
            userCacheInit(id);
            user = getUserFromCache(id);
        }
        return user;
    }
    public User findUserByName(String username) { return userMapper.selectByName(username); }
    public User findUserByEmail(String email) { return userMapper.selectByEmail(email); }
    public int updateHeaderUrl(int userId, String headerUrl) {
        cleanUserCache(userId);
        return userMapper.updateHeaderUrl(userId, headerUrl);
    }
    public int updatePassword(int userId, String password, String salt) {
        cleanUserCache(userId);
        return userMapper.updatePassword(userId, CommunityUtil.md5(password + salt));
    }

    public Map<String, Object> login(String username, String password, boolean isRemember) {
        Map<String, Object> msgs = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            msgs.put("usernameMsg", "用户名不能为空");
            return msgs;
        }
        if (StringUtils.isBlank(password)) {
            msgs.put("passwordMsg", "用户密码不能为空");
            return msgs;
        }
        User user = userMapper.selectByName(username);
        if (user == null) {
            msgs.put("usernameMsg", "用户不存在");
            return msgs;
        }
        if (user.getStatus() == 0) {
            msgs.put("usernameMsg", "用户未激活，请先激活用户");
            return msgs;
        }
        String md5 = CommunityUtil.md5(password + user.getSalt());
        if (!md5.equals(user.getPassword())) {
            msgs.put("passwordMsg", "用户密码错误");
            return msgs;
        }
        /**
         * sa-token实现认证和授权
         */
        if (isRemember) {
            StpUtil.login(user.getId(), new SaLoginModel().setTimeout(Constants.TOKEN_DURATION));
        } else {
            //  退出浏览器不保存登录状态
            StpUtil.login(user.getId(), new SaLoginModel().setIsLastingCookie(false));
        }

        return msgs;
    }

    public void logout(int userId) {
        StpUtil.logout(userId);
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
        // 访问激活服务url示例：http://localhost:8080/community/activation/100/code
        Context context = new Context();
        String url = String.format("%s%s/activation/%d/%s", domain, path, user.getId(), user.getActivationCode());
        context.setVariable("email", user.getEmail());
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.send(user.getEmail(), "community社区激活邮件", content);
        return msgs;
    }
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return Constants.ACTIVATION_REPEAT;
        }
        if (user.getActivationCode().equals(code)) {
            cleanUserCache(userId);
            userMapper.updateStatus(userId, 1);
            return Constants.ACTIVATION_SUCCESS;
        }
        return Constants.ACTIVATION_ERROR;
    }
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> msgs = new HashMap<>();
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            msgs.put("emailMsg", "该邮箱未注册！");
            return msgs;
        }
        updatePassword(user.getId(), password, user.getSalt());
        return msgs;
    }
    public void userCacheInit(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user = userMapper.selectById(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
    }
    public User getUserFromCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    public void cleanUserCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
    public List<String> getAuthorizationStatus(int userId) {
        User user = userMapper.selectById(userId);
        List<String> status = new ArrayList<>();
        if (user != null) {
            switch (user.getType()) {
                case Constants.AUTHORIZATION_USER :
                    status.add("user");
                    break;
                case Constants.AUTHORIZATION_ADMIN :
                    status.add("admin");
                    break;
                case Constants.AUTHORIZATION_MODERATOR :
                    status.add("moderator");
                    break;
            }
        }
        return status;
    }
}
