package com.xcl.controller;

import com.xcl.service.EmailService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    /**
     * 发送邮件
     * @return
     */
    @RequestMapping("/send/{email}/{code}")
    public Boolean send(@PathVariable String email,@PathVariable String code){
        System.out.println("发送验证码接口："+email+":"+code);
        if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
            System.out.println("必填参数不允许为空");
            return false;
        }
        try{
            emailService.sendEmail(email,code);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
