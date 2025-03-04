package com.example.Sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.Sample.dto.InterviewDetails;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationConsumer {
    private final JavaMailSender mailSender;

    public EmailNotificationConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "interview_schedule", groupId = "email-group")
    public void sendEmailNotification(String message) {
        System.out.println("📩 Received Kafka message: " + message);

        // Split the message to extract data
        String[] parts = message.split(",");
        if (parts.length < 4) {
            System.out.println("❌ Invalid message format!");
            return;
        }

        String candidateName = parts[0];  
        String interviewDate = parts[1];  
        String recipientEmail = parts[2]; 
        String interviewTime =parts[3];
        String interviewMode=parts[4];

        // Construct email content
//        String emailContent = "Dear " + candidateName + ",\n\n"
//                + "Your interview has been scheduled for " + interviewDate + ".\n"
//                + "Please check your inbox for further details.\n\n"
//                + "Best regards,\nHR Team";
        String emailContent = "Dear " + candidateName + ",\n\n"
                + "We are pleased to inform you that your interview has been scheduled. Please find the details below:\n\n"
                + "📅 Date: " + interviewDate + "\n"
                + "⏰ Time: " + interviewTime + "\n"
                + "🖥 Mode: " + interviewMode + "\n\n"
                + "Please ensure that you are available at the scheduled time and check your email for any additional instructions.\n"
                + "If you have any questions or need to reschedule, feel free to contact us.\n\n"
                + "Looking forward to speaking with you!\n\n"
                + "Best regards,\n"
                + "HR Team\n"
                + "📧 hr@example.com | ☎️ +1-800-123-4567";
     
        // Send email
        sendEmail(recipientEmail, "Interview Scheduled", emailContent);
    }
    


    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(body);
        mailSender.send(email);

        System.out.println("✅ Email successfully sent to: " + to);
    }
}