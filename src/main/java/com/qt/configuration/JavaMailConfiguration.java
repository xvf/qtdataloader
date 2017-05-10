package com.qt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by asrivastava on 5/9/17.
 */

/**
 * This class has configuration for the SMTP service, for sending alert and completion emails upon
 * completion of the jobs
 */
@Configuration
public class JavaMailConfiguration {
    @Value("${smtp.to.email}")
    private String toEmail;
    @Value("${smtp.from.email}")
    private String fromEmail;
    @Value("${smtp.host}")
    private String host;
    @Value("${smtp.user}")
    private String user;
    @Value("${smtp.password}")
    private String password;
    @Value("${smtp.port}")
    private Integer port;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost(host);
        javaMailSenderImpl.setUsername(user);
        javaMailSenderImpl.setPassword(password);
        javaMailSenderImpl.setProtocol("smtp");
        javaMailSenderImpl.setPort(port);
        return javaMailSenderImpl;
    }

    @Bean
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Job Status");
        mailMessage.setFrom(fromEmail);
        return mailMessage;
    }
}
