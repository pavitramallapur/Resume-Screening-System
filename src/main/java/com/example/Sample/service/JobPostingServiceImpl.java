package com.example.Sample.service;

import java.util.List;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Sample.dto.JobPosting;
import com.example.Sample.repo.JobPostingRepo;

@Service
public class JobPostingServiceImpl implements JobPostingService {

    @Autowired
    private JobPostingRepo jobPostingRepo;

    @Override
    public JobPosting saveJobPosting(JobPosting jobPosting) {
        return jobPostingRepo.save(jobPosting); // Save job posting to DB
    }

    @Override
    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepo.findAll(); // Retrieve all job postings
    }

    public JobPosting getJobPostingById(Long jobPostingId) {
        return jobPostingRepo.findById(jobPostingId).orElseThrow(() -> 
            new ResourceNotFoundException("Job Posting not found with ID: " + jobPostingId));
    }
    @Override
    public List<JobPosting> getJobPostingsByStatus(String status) {
        return jobPostingRepo.findByJobStatus(status); // Retrieve job postings by status (e.g., "active")
    }

    @Override
    public void deleteJobPosting(Long id) {
        jobPostingRepo.deleteById(id); // Delete job posting by ID
    }
}
