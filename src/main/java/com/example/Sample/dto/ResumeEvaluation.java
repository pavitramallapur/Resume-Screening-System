package com.example.Sample.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "resume_evaluations")

public class ResumeEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobPosting jobPosting;

    private float aiScore;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime evaluatedAt;
    
    

    public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Resume getResume() {
		return resume;
	}



	public void setResume(Resume resume) {
		this.resume = resume;
	}



	public JobPosting getJobPosting() {
		return jobPosting;
	}



	public void setJobPosting(JobPosting jobPosting) {
		this.jobPosting = jobPosting;
	}



	public float getAiScore() {
		return aiScore;
	}



	public void setAiScore(float aiScore) {
		this.aiScore = aiScore;
	}



	public Status getStatus() {
		return status;
	}



	public void setStatus(Status status) {
		this.status = status;
	}



	public LocalDateTime getEvaluatedAt() {
		return evaluatedAt;
	}



	public void setEvaluatedAt(LocalDateTime evaluatedAt) {
		this.evaluatedAt = evaluatedAt;
	}



	public enum Status {
        PENDING, COMPLETED
    }
}