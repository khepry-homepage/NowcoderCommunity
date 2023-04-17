package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        User loginUser = userHolder.get();
        if (user == null) {
            return "redirect:/home/index";
        }
        int likeCount = likeService.findUserLikeCount(userId);
        //  粉丝数
        long followerCount = followService.getFollowCount(userId, Constants.FOLLOW_ENTITY_TYPE_USER, true);
        //  关注者数
        long followeeCount = followService.getFollowCount(userId, Constants.FOLLOW_ENTITY_TYPE_USER, false);
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followeeCount", followeeCount);
        model.addAttribute("isMyProfile", user.getId() == loginUser.getId() ? true : false);
        model.addAttribute("isFollower", followService.isFollower(loginUser.getId(), userId, Constants.FOLLOW_ENTITY_TYPE_USER));
        return "site/profile";
    }
}
