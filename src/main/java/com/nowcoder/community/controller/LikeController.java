package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @RequestMapping(path = "/changeStatus", method = RequestMethod.POST)
    @ResponseBody
    public String changeLikeStatus(int entityType, int entityId) {
        User user = userHolder.get();
        //  当点赞目标为帖子是，entityType = 0
        int entityUserId = entityType > 0 ? commentService.findCommentById(entityId).getUserId()
                : discussPostService.findDiscussPostById(entityId).getUserId();
        if (entityUserId == user.getId()) {
            return CommunityUtil.toJSONObject(400, "不能点赞自己的帖子或评论!");
        }
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        long likeCount = likeService.findLikeCount(entityType, entityId);
        int isLike = likeService.findLikeStatusById(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("isLike", isLike);
        return CommunityUtil.toJSONObject(200, "ok", map);
    }
}
