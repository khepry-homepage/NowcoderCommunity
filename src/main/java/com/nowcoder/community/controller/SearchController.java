package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Controller
public class SearchController {
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String searchPost(@RequestParam("keyword") String keyword, Page page, Model model) {
        page.setLimit(10);
        page.setTotalRows(elasticSearchService.countContainKeyword(keyword));
        page.setPath("/search?keyword=" + keyword);
        List<DiscussPost> posts = elasticSearchService.searchPage(keyword, page.getCurrentPageId() - 1, page.getLimit());
        List<Map<String, Object>> postList = new ArrayList<>();
        for (DiscussPost post : posts) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(post.getUserId());
            long likeCount = likeService.findLikeCount(post.getId(), Constants.ENTITY_TYPE_POST);
            map.put("user", user);
            map.put("likeCount", likeCount);
            map.put("post", post);
            postList.add(map);
        }
        model.addAttribute("postList", postList);
        return "site/search";
    }
}
