package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/home", method = RequestMethod.GET)
public class HomeController {
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private MessageService messageService;
    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page) {
        User loginUser = userHolder.get();
        page.setTotalRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/home/index");
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPostsList = new ArrayList<>();
        if (discussPostsList != null) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                long likeCount = likeService.findLikeCount(Constants.ENTITY_TYPE_POST, discussPost.getId());
                User user = userService.findUserById(discussPost.getUserId());
                map.put("post", discussPost);
                map.put("user", user);
                map.put("likeCount", likeCount);
                discussPostsList.add(map);
            }
        }
        model.addAttribute("totalUnRead", 0);
        if (loginUser != null) {
            model.addAttribute("totalUnRead",
                    messageService.findNoticeUnReadCount(loginUser.getId(), null) +
                    messageService.findLetterUnReadCount(loginUser.getId(), null));
        }

        model.addAttribute("discussPosts", discussPostsList);
        return "index";
    }
    @RequestMapping(value = "/denied", method = RequestMethod.GET)
    public String getErrorPage() {
        return "error/500";
    }
}
