package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
@Component
public class PostJob implements Job {
    static Logger logger = LoggerFactory.getLogger(PostJob.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    private static final Date epoch;  //  项目运行基准时间
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2012-12-12 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化项目运行基准时间失败");
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("#############开启定时任务 - 更新帖子分数#############");
        String redisKey = RedisKeyUtil.getScheduledPostKey();
        Set<Object> postIds = redisTemplate.opsForSet().members(redisKey);
        if (postIds != null && postIds.size() != 0) {
            for (Object id : postIds) {
                this.refresh((Integer) id);
            }
            redisTemplate.delete(redisKey);
        }
        logger.info("#############结束定时任务 - 更新帖子分数#############");
    }
    public void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null || post.getStatus() == 2) {
            logger.error("帖子不存在或已被删除！");
            return;
        }
        long likeCount = likeService.findLikeCount(Constants.ENTITY_TYPE_POST, postId);
        boolean isEssence = post.getStatus() == 1 ? true : false;
        //  score = log(精华分 + 评论数 * 10 + 点赞数 * 2) + (帖子发布时间与epoch初始时间的间隔天数)
        double score = isEssence ? 75 : 0 + post.getCommentCount() * 10 + likeCount * 2;
        score = Math.log(score) + (post.getCreateTime().getTime() - epoch.getTime()) / (3600 * 24 * 1000);
        post.setScore(score);
        discussPostService.updatePostScore(postId, score);
        elasticSearchService.saveDiscussPost(post);
    }
}
