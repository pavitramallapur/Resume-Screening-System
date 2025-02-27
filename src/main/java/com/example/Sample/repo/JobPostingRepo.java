package com.example.Sample.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Sample.dto.JobPosting;

public interface JobPostingRepo extends JpaRepository<JobPosting, Long> {

}
