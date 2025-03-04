package com.example.Sample.dto;



import java.time.LocalDate;
import java.time.LocalTime;

public class InterviewDetails {
    private String candidateName;
    private LocalDate interviewDate;
    private String interviewTime;
    private String interviewMode;
    
    // Default constructor required for JSON deserialization
    public InterviewDetails() {}
    
    public InterviewDetails(String candidateName, LocalDate interviewDate,String interviewMode,String interviewTime) {
        this.candidateName = candidateName;
        this.interviewDate = interviewDate;
        this.interviewTime=interviewTime;
        this.interviewMode=interviewMode;
    }
    
    // Getters and setters
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }

	public String getInterviewTime() {
		return interviewTime;
	}

	public void setInterviewTime(String interviewTime) {
		this.interviewTime = interviewTime;
	}

	public String getInterviewMode() {
		return interviewMode;
	}

	public void setInterviewMode(String interviewMode) {
		this.interviewMode = interviewMode;
	}
    
    
}