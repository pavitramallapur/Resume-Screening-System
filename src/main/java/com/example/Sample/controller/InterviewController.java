package com.example.Sample.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Sample.config.ResponseStructure;
import com.example.Sample.exception.MissingFieldException;
import com.example.Sample.service.InterviewSchedulerProducer;

@RestController
@RequestMapping("/interview")
public class InterviewController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);

    private final InterviewSchedulerProducer schedulerProducer;

    public InterviewController(InterviewSchedulerProducer schedulerProducer) {
        this.schedulerProducer = schedulerProducer;
    }

    @PostMapping("/schedule")
    public ResponseEntity<ResponseStructure<String>> scheduleInterview(
            @RequestParam String candidateName,
            @RequestParam String interviewDate,
            @RequestParam String recipientEmail,
            @RequestParam String interviewMode,
            @RequestParam String interviewTime) {
    	
    	  if (candidateName == null || candidateName.isEmpty()) {
    	        throw new MissingFieldException("candidateName");
    	    }
    	    
    	    if (recipientEmail == null || recipientEmail.isEmpty()) {
    	        throw new MissingFieldException("recipientEmail");
    	    }
    	    if (interviewMode == null || interviewMode.isEmpty()) {
    	        throw new MissingFieldException("interviewMode");
    	    }
    	    if (interviewTime == null || interviewTime.isEmpty()) {
    	        throw new MissingFieldException("interviewTime");
    	    }
        // Validate interview date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(interviewDate, formatter);
        } catch (DateTimeParseException e) {
        	ResponseStructure<String> responseStructure=new ResponseStructure<>();
        	responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
        	responseStructure.setMessage("Invalid interview date format. Please use yyyy-MM-dd.");
        	responseStructure.setData(null);
//            return ResponseEntity.badRequest().body("Invalid interview date format. Please use yyyy-MM-dd.");
        	return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.BAD_GATEWAY);
        }

        // Construct message including recipient email
        String message = candidateName + "," + interviewDate + "," + recipientEmail + "," + interviewMode + "," + interviewTime;

        // Send message to Kafka
        schedulerProducer.scheduleInterview(message);

        // Log the scheduling
        logger.info("Interview scheduled for: {}", candidateName);
        
        ResponseStructure<String> responseStructure=new ResponseStructure<>();
    	responseStructure.setStatus(HttpStatus.CREATED.value());
    	responseStructure.setMessage("Interview scheduled successfully!");
    	responseStructure.setData(interviewDate);
    	return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.CREATED);

    }
}
