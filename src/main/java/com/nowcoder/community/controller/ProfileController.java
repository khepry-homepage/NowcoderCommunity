package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getProfile(Model model) {
        User user = userHolder.get();
        int likeCount = likeService.findUserLikeCount(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeCount);
        return "site/profile";
    }
}
