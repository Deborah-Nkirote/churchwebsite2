package com.emt.dms1.Services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.example.com"); // Replace with your SMTP server
        mailSender.setPort(587);

        mailSender.setUsername("your-email@example.com"); // Replace with your email
        mailSender.setPassword("your-email-password"); // Replace with your email password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
