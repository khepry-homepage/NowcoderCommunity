package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/follow")
public class FollowController {
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    private EventProducer producer;
    @RequestMapping(path = "/changeFollowStatus", method = RequestMethod.POST)
    @ResponseBody
    public String changeFollowStatus(int followeeId, int entityType) {
        User user = userHolder.get();
        if (entityType < Constants.ENTITY_TYPE_POST || entityType > Constants.FOLLOW_ENTITY_TYPE_USER) {
            return CommunityUtil.toJSONObject(400, "无效的请求参数 - 关注类型");
        }
        followService.follow(user.getId(), entityType, followeeId);
        //  向被关注的用户发通知
        if (followService.isFollower(user.getId(), followeeId, entityType)) {
            Event event = new Event()
                    .setEventType(Constants.EVENT_TYPE_FOLLOW)
                    .setUserId(user.getId())
                    .setEntityId(followeeId)
                    .setEntityUserId(followeeId)
                    .setEntityType(entityType);
            producer.commitEvent(event);
        }
        return CommunityUtil.toJSONObject(200, "ok");
    }
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        User loginUser = userHolder.get();
        page.setLimit(5);
        page.setPath("/follow/followees/" + userId);
        page.setTotalRows((int) followService.getFollowCount(userId, Constants.FOLLOW_ENTITY_TYPE_USER, false));
        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (followees != null) {
            for (Map<String, Object> map : followees) {
                User followeeUser = userService.findUserById(((User) map.get("user")).getId());
                map.put("user", followeeUser);
                map.put("isFollower", followService.isFollower(loginUser.getId(), followeeUser.getId(), Constants.FOLLOW_ENTITY_TYPE_USER));
            }
        }
        model.addAttribute("followees", followees);
        model.addAttribute("user", user);
        model.addAttribute("loginUserId", loginUser.getId());
        return "site/followee";
    }
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        User loginUser = userHolder.get();
        page.setLimit(5);
        page.setPath("/follow/followers/" + userId);
        page.setTotalRows((int) followService.getFollowCount(userId, Constants.FOLLOW_ENTITY_TYPE_USER, true));
        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (followers != null) {
            for (Map<String, Object> map : followers) {
                User followeeUser = userService.findUserById(((User) map.get("user")).getId());
                map.put("user", followeeUser);
                map.put("isFollower", followService.isFollower(loginUser.getId(), followeeUser.getId(), Constants.FOLLOW_ENTITY_TYPE_USER));
            }
        }
        model.addAttribute("followers", followers);
        model.addAttribute("user", user);
        model.addAttribute("loginUserId", loginUser.getId());
        return "site/follower";
    }
}
