package com.example.Sample.dto;



import java.time.LocalDate;

public class InterviewDetails {
    private String candidateName;
    private LocalDate interviewDate;
    
    // Default constructor required for JSON deserialization
    public InterviewDetails() {}
    
    public InterviewDetails(String candidateName, LocalDate interviewDate) {
        this.candidateName = candidateName;
        this.interviewDate = interviewDate;
    }
    
    // Getters and setters
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }
}