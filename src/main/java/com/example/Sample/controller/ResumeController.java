package com.example.Sample.controller;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.dto.JobPosting;
import com.example.Sample.dto.Resume;
import com.example.Sample.service.JobPostingService;
import com.example.Sample.service.ResumeService;
import com.example.Sample.util.ResumeJobMatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;
    
    @Autowired
    private JobPostingService jobPostingService;
    
    @Autowired
    private ResumeJobMatcher resumeJobMatcher;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @PostMapping("/upload")
    public String uploadResume(@RequestParam String candidateName,
                               @RequestParam String email,
                               @RequestParam String jobTitle,
                               @RequestParam String status,
                               @RequestParam("file") MultipartFile file) throws java.io.IOException {
        return processResume(candidateName, email, jobTitle, status, file);
    }

    @PostMapping("/uploadMultiple")
    public List<String> uploadMultipleResumes(@RequestParam String candidateName,
                                              @RequestParam String email,
                                              @RequestParam String jobTitle,
                                              @RequestParam String status,
                                              @RequestParam("files") MultipartFile[] files) {
        List<String> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            executorService.submit(() -> {
                try {
                    String response = processResume(candidateName, email, jobTitle, status, file);
                    synchronized (responses) {
                        responses.add(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return responses;
    }

    private String processResume(String candidateName, String email, String jobTitle, String status, MultipartFile file) {
        try {
            String filePath = saveUploadedFile(file);
            String extractedText = extractTextFromPDF(filePath);
            Resume savedResume = resumeService.saveResume(candidateName, email, jobTitle, filePath, status, extractedText);
            return "Resume uploaded successfully with ID: " + savedResume.getId();
        } catch (Exception e) {
            return "Error processing resume: " + e.getMessage();
        }
    }

    private String extractTextFromPDF(String filePath) throws java.io.IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private String saveUploadedFile(MultipartFile file) throws java.io.IOException {
        String uploadDir = "/home/pavitra/Documents/Resumes/";
        String filePath = uploadDir + file.getOriginalFilename();
        File uploadFile = new File(filePath);
        file.transferTo(uploadFile);
        return filePath;
    }
//
//    @GetMapping("/rank/{resumeId1}{resumeId2/{jobPostingId}")
//    public double rankResume(@PathVariable Long resumeId, @PathVariable Long jobPostingId) {
//        Resume resume = resumeService.getResumeById(resumeId);
//        JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);
//        if (resume == null || jobPosting == null) {
//            throw new ResourceNotFoundException("Resume or Job Posting not found");
//        }
//        return resumeJobMatcher.matchResumeToJob(resume, jobPosting);
//    }
    @GetMapping("/rank/{resumeId1}/{resumeId2}/{jobPostingId}")
    public String rankResumes(@PathVariable Long resumeId1, 
                              @PathVariable Long resumeId2, 
                              @PathVariable Long jobPostingId) throws ExecutionException, InterruptedException {
        
        // Fetch job posting
        JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);
        if (jobPosting == null) {
            throw new ResourceNotFoundException("Job Posting not found");
        }

        // Fetch resumes in parallel
        CompletableFuture<Resume> resumeFuture1 = CompletableFuture.supplyAsync(() -> resumeService.getResumeById(resumeId1));
        CompletableFuture<Resume> resumeFuture2 = CompletableFuture.supplyAsync(() -> resumeService.getResumeById(resumeId2));

        Resume resume1 = resumeFuture1.get();
        Resume resume2 = resumeFuture2.get();

        if (resume1 == null || resume2 == null) {
            throw new ResourceNotFoundException("One or both resumes not found");
        }

        // Compute AI scores in parallel
        CompletableFuture<Double> scoreFuture1 = CompletableFuture.supplyAsync(() -> resumeJobMatcher.matchResumeToJob(resume1, jobPosting));
        CompletableFuture<Double> scoreFuture2 = CompletableFuture.supplyAsync(() -> resumeJobMatcher.matchResumeToJob(resume2, jobPosting));

        double score1 = scoreFuture1.get();
        double score2 = scoreFuture2.get();

        return "AI Scores:\nResume 1: " + score1 + "%\nResume 2: " + score2 + "%";
    }


    @GetMapping("/all")
    public List<Resume> getAllResumes() {
        return resumeService.getAllResumes();
    }

    @GetMapping("/jobpostings/{id}")
    public JobPosting getJobPosting(@PathVariable Long id) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        if (jobPosting == null) {
            throw new ResourceNotFoundException("Job Posting not found");
        }
        return jobPosting;
    }
}
