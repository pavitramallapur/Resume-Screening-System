package com.example.Sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Sample.dto.JobPosting;
import com.example.Sample.service.JobPostingService;

import java.util.List;

@RestController
@RequestMapping("/api/job-postings")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    
    @PostMapping
    public ResponseEntity<JobPosting> createJobPosting(@RequestBody JobPosting jobPosting) {
        JobPosting savedJobPosting = jobPostingService.saveJobPosting(jobPosting);
        return new ResponseEntity<>(savedJobPosting, HttpStatus.CREATED);
    }

   
    @GetMapping
    public ResponseEntity<List<JobPosting>> getAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingService.getAllJobPostings();
        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<JobPosting> getJobPostingById(@PathVariable Long id) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        if (jobPosting != null) {
            return new ResponseEntity<>(jobPosting, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobPosting>> getJobPostingsByStatus(@PathVariable String status) {
        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByStatus(status);
        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable Long id) {
        jobPostingService.deleteJobPosting(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
