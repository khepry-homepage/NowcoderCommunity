package com.nowcoder.community.controller;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.*;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;
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
    @RequestMapping(path = "/getMyPost/{userId}", method = RequestMethod.GET)
    public String getMyPosts(@PathVariable("userId") int userId, Model model, Page page) {
        User loginUser = userHolder.get();
        page.setLimit(5);
        page.setPath("/profile/getMyPost/" + userId);
        page.setTotalRows(discussPostService.findDiscussPostRows(userId));
        List<DiscussPost> posts = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> postList = new ArrayList<>();
        for (DiscussPost post : posts) {
            Map<String, Object> map = new HashMap<>();
            long likeCount = likeService.findLikeCount(Constants.ENTITY_TYPE_POST, post.getId());
            map.put("post", post);
            map.put("likeCount", likeCount);
            postList.add(map);
        }
        model.addAttribute("postList", postList);
        model.addAttribute("userId", userId);
        model.addAttribute("loginUserId", loginUser.getId());
        model.addAttribute("isMyProfile", loginUser.getId() == userId ? true : false);
        return "site/my-post";
    }
    @RequestMapping(path = "/getMyReply/{userId}", method = RequestMethod.GET)
    public String getMyReply(@PathVariable("userId") int userId, Model model, Page page) {
        User loginUser = userHolder.get();
        page.setLimit(5);
        page.setPath("/profile/getMyReply/" + userId);
        page.setTotalRows(commentService.findCommentRows(userId, Constants.ENTITY_TYPE_POST, 0));
        List<Comment> comments = commentService.findComments(userId, Constants.ENTITY_TYPE_POST, 0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
            map.put("comment", comment);
            map.put("post", post);
            commentList.add(map);
        }
        model.addAttribute("commentList", commentList);
        model.addAttribute("userId", userId);
        model.addAttribute("loginUserId", loginUser.getId());
        model.addAttribute("isMyProfile", loginUser.getId() == userId ? true : false);
        return "site/my-reply";
    }
}
