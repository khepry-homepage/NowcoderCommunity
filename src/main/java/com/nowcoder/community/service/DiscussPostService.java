package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.SensitiveFilter;
import jakarta.annotation.PostConstruct;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    LoadingCache<String, List<DiscussPost>> postListCache;
    LoadingCache<Integer, Integer> postRowsCache;
    @Value("${caffeine.maxSize}")
    private int maxSize;
    @PostConstruct
    public void init() {
        //  热帖缓存（按页缓存）
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        logger.debug("miss post list in caffeine cache.");
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("传递无效缓存key值！");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        //  可添加二级缓存：本地缓存→redis缓存，此处为本地缓存miss→访问db
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, Constants.POST_ORDER_MODE_HOTTEST);
                    }
                });
        //  热帖总页面数缓存
        postRowsCache = Caffeine.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer userId) throws Exception {
                        logger.debug("miss post rows in caffeine cache.");
                        return discussPostMapper.selectDiscussPostRows(userId);
                    }
                });
    }
    public DiscussPost findDiscussPostById(int id) { return discussPostMapper.selectDiscussPostById(id); }
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        //  如果是热帖排行，查询本地缓存
        if (userId == 0 && orderMode == Constants.POST_ORDER_MODE_HOTTEST) {
            return postListCache.get(offset + ":" + limit);
        }
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }
    public int findDiscussPostRows(int userId) {
        //  如果是热帖排行，查询本地缓存
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    public int addDiscussPost(DiscussPost post) {
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(sensitiveFilter.filterMatch(post.getTitle()));
        post.setContent(sensitiveFilter.filterMatch(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
    public int updatePostType(int id, int type) {
        return discussPostMapper.updatePostType(id, type);
    }
    public int updatePostStatus(int id, int status) {
        return discussPostMapper.updatePostStatus(id, status);
    }
    public int updatePostScore(int id, double score) { return discussPostMapper.updatePostScore(id, score); }
}
