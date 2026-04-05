package com.securitygateway.nextstep.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Separate Email Service for File Management System
 * This does NOT modify the existing EmailService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileNotificationService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendFileUploadNotification(String to, String subject, String messageContent) {
        try {
            log.info("Sending file upload notification to {}", to);

            String senderName = "Security Gateway - File System";
            String from = "kavishkadewduni@gmail.com";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from, senderName);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = "<html>"
                    + "<body>"
                    + "<pre style='font-family: Arial, sans-serif; font-size: 14px;'>"
                    + messageContent
                    + "</pre>"
                    + "<p style='color:gray; font-size:12px;'>(This is an auto generated email, so please do not reply back.)</p>"
                    + "<img src='cid:policeOfficerImage' alt='Police Officer' style='width:100px; height:auto;'/>"
                    + "</body>"
                    + "</html>";
            helper.setText(htmlContent, true);

            ClassPathResource image = new ClassPathResource("static/security-removebg-preview.png");
            helper.addInline("policeOfficerImage", image);

            javaMailSender.send(message);
            log.info("File upload notification sent successfully to {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send file upload notification to {}: {}", to, e.getMessage());
        }
    }
}