package com.example.Sample.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Sample.dto.JobPosting;

public interface JobPostingRepo extends JpaRepository<JobPosting, Long> {
    // You can add custom query methods here if needed (e.g., find by status, title, etc.)
    List<JobPosting> findByJobStatus(String jobStatus);
    List<JobPosting> findByLocation(String location);
}
