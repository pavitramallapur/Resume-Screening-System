package com.example.Sample.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;
    private String email;
    private String jobTitle;
    private Double aiScore;
    private String filePath;
    private String status; // "Pending", "Reviewed", "Shortlisted"

    @Lob
    private String extractedText;
    
    @CreatedDate
    private LocalDateTime uploadedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Double getAiScore() {
		return aiScore;
	}

	public void setAiScore(Double aiScore) {
		this.aiScore = aiScore;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExtractedText() {
		return extractedText;
	}

	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

	public Resume(String candidateName, String email, String jobTitle, Double aiScore, String filePath,
			String status, String extractedText, LocalDateTime uploadedAt) {
		super();
		this.id = id;
		this.candidateName = candidateName;
		this.email = email;
		this.jobTitle = jobTitle;
		this.aiScore = aiScore;
		this.filePath = filePath;
		this.status = status;
		this.extractedText = extractedText;
		this.uploadedAt = uploadedAt;
	}
    
    
    
}
