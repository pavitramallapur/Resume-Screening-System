package com.example.Sample.service;

import java.util.List;
import com.example.Sample.dto.JobPosting;

public interface JobPostingService {

    JobPosting saveJobPosting(JobPosting jobPosting);

    List<JobPosting> getAllJobPostings();

    JobPosting getJobPostingById(Long id); 

    List<JobPosting> getJobPostingsByStatus(String status); 

    void deleteJobPosting(Long id); 
}
