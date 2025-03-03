package com.example.Sample.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Sample.dto.InterviewDetails;
//import com.example.Sample.model.InterviewDetails;
import com.example.Sample.service.InterviewSchedulerProducer;
//import com.example.Sample.service.InterviewService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview")
public class InterviewController {

    private final InterviewSchedulerProducer schedulerProducer;

    public InterviewController(InterviewSchedulerProducer schedulerProducer) {
        this.schedulerProducer = schedulerProducer;
    }

    @PostMapping("/schedule")
    public String scheduleInterview(
            @RequestParam String candidateName,
            @RequestParam String interviewDate,
            @RequestParam String recipientEmail) {

        // Construct message including recipient email
        String message = candidateName + "," + interviewDate + "," + recipientEmail;

        // Send message to Kafka
        schedulerProducer.scheduleInterview(message);

        return "Interview scheduled successfully!";
    }

}


