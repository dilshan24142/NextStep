package com.securitygateway.nextstep.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

@Configuration
@EnableRetry
@EnableAsync
public class MailConfiguration {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465);  // Changed from 587 to 465
        mailSender.setUsername("suranimalaravinsha@gmail.com");
        mailSender.setPassword("colmceperjztwwlw");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");  // Enable SSL
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Timeouts
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.writetimeout", "30000");

        // Force TLS 1.2 (required by Gmail)
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Socket factory for SSL
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "465");

        // Fix for EHLO
        props.put("mail.smtp.localhost", "localhost");

        props.put("mail.debug", "true");

        return mailSender;
    }
}