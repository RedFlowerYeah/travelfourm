package com.travelfourm.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author 34612
 * 邮件工具类
 */
@Component
public class MailClient {

    /**
     * 获取当前类的日志*/
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    /**
     * 注入SpringBoot-mail的使用接口*/
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 将application.proprities的自定义外部值动态注入到Bean中*/
    @Value("${spring.mail.username}")
    private String from;


    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败:" + e.getMessage());
        }
    }
}
