package com.example.Sample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InterviewSchedulerProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "interview_schedule";

    public InterviewSchedulerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void scheduleInterview(String interviewDetails) {
        kafkaTemplate.send(TOPIC, interviewDetails);
        System.out.println("✅ Interview scheduled: " + interviewDetails);
    }
}

