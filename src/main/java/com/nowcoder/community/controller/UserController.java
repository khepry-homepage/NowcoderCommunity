package com.nowcoder.community.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserHolder userHolder;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${aliyun.oss.bucketDomain}")
    private String bucketDomain;
    @Value("${community.upload.header}")
    private String uploadDir;
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String setting() {
        return "site/setting";
    }

    /**
     * 上传文件到阿里云OSS
     * @param headerImage
     * @param model
     * @return
     */
    @RequestMapping(path = "/uploadHeader", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "未上传图片！");
            return "site/setting";
        }
        User user = userHolder.get();
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);  //  获取图片格式
        filename = CommunityUtil.generateUUID() + "." + suffix;
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如header/user-101.jpg。
        String objectName = uploadDir + "/" + filename;
        String headerUrl = bucketDomain + "/" + objectName;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        int uploadStatus = 0;
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, headerImage.getInputStream());
            // 设置该属性可以返回response。如果不设置，则返回的response为空。
            putObjectRequest.setProcess("true");
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // 如果上传成功，则返回200。
            uploadStatus = result.getResponse().getStatusCode();
        }
        catch (IOException e) {
            logger.error("Error Message:" + e.getMessage());
        }
        catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            logger.error("Error Message:" + oe.getErrorMessage());
            logger.error("Error Code:" + oe.getErrorCode());
            logger.error("Request ID:" + oe.getRequestId());
            logger.error("Host ID:" + oe.getHostId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        // 如果上传成功，则返回200。
        if (uploadStatus == 200) {
            userService.updateHeaderUrl(user.getId(), headerUrl);
            return "redirect:/home/index";
        } else {
            model.addAttribute("error", "上传头像失败！");
            return "site/setting";
        }
    }
//    //  上传文件到本地服务器
//    @Deprecated
//    @RequestMapping(path = "/uploadHeader", method = RequestMethod.POST)
//    public String uploadHeader(MultipartFile headerImage, Model model) {
//        if (headerImage == null) {
//            model.addAttribute("error", "未上传图片！");
//            return "site/setting";
//        }
//        String dirname = System.getProperties().getProperty("user.dir");
//        String filename = headerImage.getOriginalFilename();
//        String suffix = filename.substring(filename.lastIndexOf('.') + 1);  //  获取图片格式
//        filename = CommunityUtil.generateUUID() + "." + suffix;
//        File dest = new File(dirname + uploadLocation + "/" + filename);
//        try {
//            headerImage.transferTo(dest);
//        } catch (IOException e) {
//            logger.error("上传文件失败!");
//            throw new RuntimeException("上传文件失败: ", e);
//        }
//        //  更新用户表headerUrl属性
//        User user = userHolder.get();
//        String headerUrl = domain + contextPath + "/user/header/" + filename;
//        userService.updateHeaderUrl(user.getId(), headerUrl);
//        return "redirect:/home/index";
//    }
//    @Deprecated
//    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
//    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
//        String dirname = System.getProperties().getProperty("user.dir");
//        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
//        response.setContentType("image/" + suffix);
//        filename = dirname + uploadLocation + "/" + filename;
//        try(
//            FileInputStream input = new FileInputStream(filename);
//            OutputStream output = response.getOutputStream();
//        ) {
//            int bytesRead = 0;
//            byte[] imageData = new byte[1024];
//            while ((bytesRead = input.read(imageData)) != -1) {
//                output.write(imageData, 0, bytesRead);
//            }
//        } catch (IOException e) {
//            logger.error("读取头像失败: ", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
    @RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
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
