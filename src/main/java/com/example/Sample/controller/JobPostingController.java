package com.example.Sample.controller;

import org.hibernate.query.NativeQuery.ReturnableResultNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Sample.config.ResponseStructure;
import com.example.Sample.dto.JobPosting;
import com.example.Sample.exception.IdNotFoundException;
import com.example.Sample.exception.MissingFieldException;
import com.example.Sample.service.JobPostingService;

import java.util.List;

@RestController
@RequestMapping("/api/job-postings")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    
    @PostMapping
    public ResponseEntity<ResponseStructure<JobPosting>> createJobPosting(@RequestBody JobPosting jobPosting) {
    	if (jobPosting.getRequiredSkills() == null || jobPosting.getRequiredSkills().isEmpty()) {
    	    throw new MissingFieldException("requiredSkills");
    	}
        if(jobPosting.getCompanyName()==null || jobPosting.getCompanyName().isEmpty()) {
        	throw new MissingFieldException("CompanyName Must Be Required But it ");
        }
        if(jobPosting.getLocation()==null || jobPosting.getLocation().isEmpty()) {
        	throw new MissingFieldException("location");
        }
        if(jobPosting.getDescription()== null || jobPosting.getDescription().isEmpty()) {
        	throw new MissingFieldException("description");
        }
        
        JobPosting savedJobPosting = jobPostingService.saveJobPosting(jobPosting);
        ResponseStructure<JobPosting> responseStructure=new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Job-Posting Sucuessfully!");
        responseStructure.setData(savedJobPosting);
        return new ResponseEntity<ResponseStructure<JobPosting>>(responseStructure,HttpStatus.CREATED);
//        return new ResponseEntity<>(savedJobPosting, HttpStatus.CREATED);
    }

   
    @GetMapping
    public ResponseEntity<ResponseStructure<List<JobPosting>>> getAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingService.getAllJobPostings();
        ResponseStructure<List<JobPosting>>responseStructure=new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("All Job-Posts Found Sucuessfully!");
        responseStructure.setData(jobPostings);
        return new ResponseEntity<ResponseStructure<List<JobPosting>>>(responseStructure, HttpStatus.CREATED);
//        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<JobPosting>> getJobPostingById(@PathVariable Long id) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        if (jobPosting != null) {
            ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
            responseStructure.setStatus(HttpStatus.OK.value()); // Changed to HttpStatus.OK
            responseStructure.setMessage("Job posting found successfully with the given ID");
            responseStructure.setData(jobPosting);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK); // Changed to HttpStatus.OK
        } else {
            throw new IdNotFoundException("Job posting with given ID not found");
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
