package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(102);
        assert (user.getUsername().equals("guanyu"));
        user = userMapper.selectByName("guanyu");
        assert (user.getEmail().equals("nowcoder102@sina.com"));
        user = userMapper.selectByEmail("nowcoder102@sina.com");
        assert (user.getUsername().equals("guanyu"));
    }
    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("khepry");
        user.setPassword("123456");
        user.setEmail("12345@sina.com");
        user.setSalt("12312");
        user.setStatus(1);
        user.setType(0);
        user.setHeaderUrl("www.baidu.com");
        user.setCreateTime(new Date());
        int row = userMapper.insertUser(user);
        assert (row == 1);
    }
    @Test
    public void testUpdateUser() {
        userMapper.updateStatus(149, 2);
        userMapper.updateType(149, 1);
        userMapper.updateHeaderUrl(149, "www.baidu.com");
    }

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(138, 0, 100);
        for(DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

}
