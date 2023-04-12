package com.nowcoder.community.controller;

import com.nowcoder.community.Annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.UserHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserHolder userHolder;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.upload.location}")
    private String uploadLocation;
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String setting() {
        return "site/setting";
    }
    @LoginRequired
    @RequestMapping(path = "/uploadHeader", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "未上传图片！");
            return "site/setting";
        }
        String dirname = System.getProperties().getProperty("user.dir");
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);  //  获取图片格式
        filename = CommunityUtil.generateUUID() + "." + suffix;
        File dest = new File(dirname + uploadLocation + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败!");
            throw new RuntimeException("上传文件失败: ", e);
        }
        //  更新用户表headerUrl属性
        User user = userHolder.get();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeaderUrl(user.getId(), headerUrl);
        return "redirect:/home/index";
    }
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        String dirname = System.getProperties().getProperty("user.dir");
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);
        filename = dirname + uploadLocation + "/" + filename;
        try(
            FileInputStream input = new FileInputStream(filename);
            OutputStream output = response.getOutputStream();
        ) {
            int bytesRead = 0;
            byte[] imageData = new byte[1024];
            while ((bytesRead = input.read(imageData)) != -1) {
                output.write(imageData, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: ", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
    @LoginRequired
    public String resetPassword(Model model, String oldPassword, String newPassword) {
        User user = userHolder.get();
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
            model.addAttribute("oldPasswordMsg", "原密码填写不正确!");
            return "site/setting";
        }
        if (oldPassword.equals(newPassword)) {
            model.addAttribute("newPasswordMsg", "新密码不能与旧密码相同!");
            return "site/setting";
        }
        userService.updatePassword(user.getId(), newPassword, user.getSalt());
        return "redirect:/logout";
    }
}
