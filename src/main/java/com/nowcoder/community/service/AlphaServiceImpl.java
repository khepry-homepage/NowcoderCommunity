package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AlphaServiceImpl implements AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Override
    public String getUserInfo() {
        return alphaDao.find();
    }
    @PostConstruct
    private void init() {
        System.out.println("initialize member..." + simpleDateFormat.format(new Date()));
    }
    @PreDestroy
    private void destroy() {
        System.out.println("destroy member..." + simpleDateFormat.format(new Date()));
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object saveAlpha() {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        user.setSalt("test1");
        user.setHeaderUrl("http://images.nowcoder.com/head/1t.png");
        user.setCreateTime(new Date());
        user.setEmail("123456@qq.com");
        userMapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setTitle("迎新");
        post.setUserId(user.getId());
        post.setContent("欢迎新用户");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);
        Integer.valueOf("abc"); //  异常发生处, 使用事务确保回滚回执行前状态
        return "ok";
    }
    public Object saveAlphaProgram() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                User user = new User();
                user.setUsername("test1");
                user.setPassword("test1");
                user.setSalt("test1");
                user.setHeaderUrl("http://images.nowcoder.com/head/1t.png");
                user.setCreateTime(new Date());
                user.setEmail("123456@qq.com");
                userMapper.insertUser(user);

                DiscussPost post = new DiscussPost();
                post.setTitle("迎新");
                post.setUserId(user.getId());
                post.setContent("欢迎新用户");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);
                Integer.valueOf("abc"); //  异常发生处, 使用事务确保回滚回执行前状态
                return "ok";
            }
        });
    }

}
