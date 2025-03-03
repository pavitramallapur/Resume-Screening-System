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
        if (parts.length < 3) {
            System.out.println("❌ Invalid message format!");
            return;
        }

        String candidateName = parts[0];  // Example: "John"
        String interviewDate = parts[1];  // Example: "2025-03-01"
        String recipientEmail = parts[2]; // Example: "john@example.com"

        // Construct email content
//        String emailContent = "Dear " + candidateName + ",\n\n"
//                + "Your interview has been scheduled for " + interviewDate + ".\n"
//                + "Please check your inbox for further details.\n\n"
//                + "Best regards,\nHR Team";
        String subject = "Interview Scheduled – Your Upcoming Interview";
        String emailContent = "Dear " + candidateName + ",\n\n"
                + "We are pleased to inform you that your interview with [Company Name] has been scheduled.\n\n"
                + "📅 Date: " + interviewDate + "\n"
                + "⏰ Time: [Interview Time]\n"
                + "📍 Location: [Office Address] / [Online Meeting Link]\n"
                + "💼 Position: [Job Title]\n\n"
                + "Please ensure that you are available at the scheduled time. If you have any questions or need to reschedule, kindly contact us at [HR Email] or [HR Phone Number].\n\n"
                + "We look forward to meeting you!\n\n"
                + "Best regards,\n"
                + "[Your Name]\n"
                + "[Your Designation]\n"
                + "[Company Name]\n"
                + "[Company Email] | [Company Phone]";

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