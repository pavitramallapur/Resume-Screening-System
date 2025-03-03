package com.example.Sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    public void sendSimpleMessage(String to, String subject, String text) {
        // Mock implementation - just log the email details
        logger.info("Sending email to: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Content: {}", text);
        logger.info("Email sent successfully (mock)");
    }
}