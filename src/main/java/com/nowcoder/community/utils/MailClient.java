package com.nowcoder.community.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@Component
public class MailClient {
    @Autowired
    private JavaMailSenderImpl mailSender;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${spring.mail.username}")
    private String from;

    public void send(String to, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMailMessage = new MimeMessageHelper(mimeMessage,true);//打开多文件传送
            mimeMailMessage.setTo(to);//目的邮箱
            mimeMailMessage.setSubject(subject);//主题
            mimeMailMessage.setFrom(from);//发送方
            mimeMailMessage.setText(content,true);//支持html
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送邮件失败：", e.getMessage());
        }

    }
}
