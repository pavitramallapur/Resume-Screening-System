package com.example.Sample.service;

import java.util.List;
import com.example.Sample.dto.JobPosting;

public interface JobPostingService {

    JobPosting saveJobPosting(JobPosting jobPosting); // Save a job posting

    List<JobPosting> getAllJobPostings(); // Get all job postings

    JobPosting getJobPostingById(Long id); // Get job posting by ID

    List<JobPosting> getJobPostingsByStatus(String status); // Get job postings by status

    void deleteJobPosting(Long id); // Delete job posting by ID
}
