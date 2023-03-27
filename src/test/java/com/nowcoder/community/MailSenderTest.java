package com.nowcoder.community;

import com.nowcoder.community.utils.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailSenderTest {
    @Autowired
    private MailClient mailSenderService;
    @Test
    public void testSendMail() {
        mailSenderService.send("qwe1415717182@163.com", "test", "test");
    }
}
