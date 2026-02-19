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
        mailSender.setPort(587);

        mailSender.setUsername("shashinsandeepa2002@gmail.com"); // sudessha , change with yours
        mailSender.setPassword("vdhujlkdlcrbxzab");// shashin

        mailSender.setUsername("shashinsandeepa18@gmail.com"); // sudessha , change with yours
        mailSender.setPassword("vdhujlkdlcrbxzab");

        mailSender.setUsername("sandeepashashin2002@gmail.com"); // sudessha , change with yours
        mailSender.setPassword("ndajmbcofomlrzxr");



        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
