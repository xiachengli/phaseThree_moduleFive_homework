package com.xcl.service.impl;

import com.xcl.service.EmailService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Override
    public boolean sendEmail(String email, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("18508534589@163.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("验证码");
        simpleMailMessage.setText("验证码"+code);

        mailSender.send(simpleMailMessage);
        return true;
    }
}
