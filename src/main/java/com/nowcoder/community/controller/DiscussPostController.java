package com.nowcoder.community.controller;


import com.nowcoder.community.Annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserHolder userHolder;

    @RequestMapping(path = "/publish", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public String publish(String title, String content) {
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.toJSONObject(400, "标题和正文不能为空！");
        }
        User user = userHolder.get();
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return CommunityUtil.toJSONObject(200, "帖子发布成功！");
    }
    @RequestMapping(path = "/detail/{userId}/{id}", method = RequestMethod.GET)
    @LoginRequired
    public String getDetail(@PathVariable("userId") int userId, @PathVariable("id") int id, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostDetail(id);
        //  帖子已失效
        if (post == null) {
            model.addAttribute("msg", "该帖子已被删除！");
            model.addAttribute("target", "/home/index");
            return "site/operate-result";
        }
        //  传递的用户id非帖子发布者, 强制重定向回首页
        if (post.getUserId() != userId) {
            return "index";
        }
        User user = userService.findUserById(userId);
        //  用户已注销
        if (user == null) {
            model.addAttribute("msg", "该用户已注销, 无法查看帖子详情！");
            model.addAttribute("target", "/home/index");
            return "site/operate-result";
        }
        page.setTotalRows(post.getCommentCount());
        page.setPath("/discuss/detail/" + userId + "/" + id);
        page.setLimit(5);
        User loginUser = userHolder.get();

        //  帖子主体评论列表
        List<Comment> comments = commentService.findComments(Constants.ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        /**
         * [
         *  { comment, subComment },
         *  { comment, subComment }
         * ]
         */
        if (comments != null) {
            List<Map<String, Object>> commentList = new ArrayList<>();
            for (Comment comment : comments) {
                Map<String, Object> newComment = new HashMap<>();
                newComment.put("comment", comment);
                newComment.put("user", userService.findUserById(comment.getUserId()));
                List<Comment> subComments = commentService.findComments(Constants.ENTITY_TYPE_COMMENT, comment.getId(),
                        0, Integer.MAX_VALUE);
                // 评论是否有回复
                if (subComments != null && !subComments.isEmpty()) {
                    //  缓存用户信息
                    Map<Integer, User> userCache = new HashMap<>();
                    //  评论回复列表
                    List<Map<String, Object>> subCommentList = new ArrayList<>();
                    for (Comment subComment : subComments) {
                        Map<String, Object> newSubComment = new HashMap<>();
                        //  回复内容
                        newSubComment.put("reply", subComment);
                        //  回复的用户
                        int speakerId = subComment.getUserId();
                        User speaker  = userService.findUserById(speakerId);
                        newSubComment.put("user", speaker);
                        userCache.put(speakerId, speaker);
                        //  回复的目标评论id
                        int targetId = subComment.getTargetId();
                        //  如果是评论区对某人的回复
                        if (targetId != 0) {
                            Comment targetComment = commentService.findComent(targetId);
                            int targetUserId = targetComment.getUserId();
                            newSubComment.put("target", userCache.containsKey(targetUserId) ? userCache.get(targetUserId) : userService.findUserById(targetUserId));
                        }
                        subCommentList.add(newSubComment);
                    }
                    newComment.put("subComments", subCommentList);
                } else {
                    newComment.put("subComments", null);
                }
                commentList.add(newComment);
            }
            model.addAttribute("comments", commentList);
        } else {
            model.addAttribute("comments", null);
        }
        model.addAttribute("publisher", user);
        model.addAttribute("post", post);
        model.addAttribute("loginUser", loginUser);

        return "site/discuss-detail";
    }
    @RequestMapping(path = "/postComment", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String postComment(int entityType, int entityId, int targetId, String content) {
        User user = userHolder.get();
        if (user == null) {
            return CommunityUtil.toJSONObject(403, "请先登录！");
        }
        if (entityType != Constants.ENTITY_TYPE_COMMENT && entityType != Constants.ENTITY_TYPE_POST) {
            return CommunityUtil.toJSONObject(400, "无效请求参数！");
        }
        if (StringUtils.isBlank(content)) {
            return CommunityUtil.toJSONObject(400, "评论内容不能为空！");
        }
        Comment comment = new Comment();
        //  如果是评论，判断该帖子是否存在
        if (entityType == Constants.ENTITY_TYPE_POST) {
            DiscussPost post = discussPostService.findDiscussPostDetail(entityId);
            if (post == null) {
                return CommunityUtil.toJSONObject(400, "非法请求！");
            }
            // 更新帖子的评论数
            discussPostService.updateCommentCount(entityId, post.getCommentCount() + 1);
        } else {    //   如果是回复，判断回复的评论区以及回复的目标评论（有的话）是否存在
            Comment entityComment = commentService.findComent(entityId);
            //  回复的评论区是否存在
            if (entityComment == null) {
                return CommunityUtil.toJSONObject(400, "非法请求！");
            }
            //  回复的目标评论是否存在
            if (targetId != 0) {
                List<Comment> subComments = commentService.findComments(Constants.ENTITY_TYPE_COMMENT,
                        entityId, 0, Integer.MAX_VALUE);
                if (subComments == null) {
                    return CommunityUtil.toJSONObject(400, "非法请求！");
                }
                for (Comment subComment : subComments) {
                    if (subComment.getId() == targetId) {
                        comment.setTargetId(targetId);
                        break;
                    }
                }
                //  回复的目标评论不存在
                if (comment.getTargetId() == 0) {
                    return CommunityUtil.toJSONObject(400, "非法请求！");
                }
            }
        }
        comment.setUserId(user.getId());
        comment.setEntityType(entityType);
        comment.setEntityId(entityId);
        comment.setContent(content);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return CommunityUtil.toJSONObject(200, "success！");
    }
}
