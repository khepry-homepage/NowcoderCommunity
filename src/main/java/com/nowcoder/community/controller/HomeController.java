package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    //  请求接口参数列表里的类对象会自动实例化并注入model中，若传递同名类变量属性值会调用setter方法为其赋值
    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        if (orderMode != Constants.POST_ORDER_MODE_LATEST && orderMode != Constants.POST_ORDER_MODE_HOTTEST) {
            throw new IllegalArgumentException("携带非法参数orderMode！");
        }
        User loginUser = userHolder.get();
        page.setTotalRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/home/index?orderMode=" + orderMode);
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
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
        model.addAttribute("orderMode", orderMode);
        return "index";
    }
    @RequestMapping(value = "/denied", method = RequestMethod.GET)
    public String getErrorPage() {
        return "error/500";
    }
}
