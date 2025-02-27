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

import io.jsonwebtoken.io.IOException;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;
    
    @Autowired
    private JobPostingService jobPostingService;
    
    @Autowired
    private ResumeJobMatcher resumeJobMatcher;

    // Endpoint for uploading a resume
//    @PostMapping("/upload")
//    public Resume uploadResume(@RequestBody Resume resume) {
//        // Save and return the resume object (assuming 'saveResume' handles saving and validation)
//        return resumeService.saveResume(resume);
//    }
    @PostMapping("/upload")
    public String uploadResume(@RequestParam String candidateName,
                               @RequestParam String email,
                               @RequestParam String jobTitle,
                               @RequestParam String status,
                               @RequestParam("file") MultipartFile file) throws java.io.IOException {

        // Save the file and get the path
        String filePath = saveUploadedFile(file);

        // Extract text from the PDF file
        String extractedText = extractTextFromPDF(filePath);

        // Save resume data to the database
        Resume savedResume = resumeService.saveResume(candidateName, email, jobTitle,  filePath, status, extractedText);

        return "Resume uploaded successfully with ID: " + savedResume.getId() + 
               "\nExtracted Text: \n" + extractedText;
    }
  
	private String extractTextFromPDF(String filePath) throws java.io.IOException {
        try {
            File file = new File(filePath);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF.";
        }
    }
	
	private String saveUploadedFile(MultipartFile file) {
        try {
            String uploadDir = "/home/pavitra/Documents/Resumes";  
            String fileName = file.getOriginalFilename();
            
            if (fileName == null || fileName.isEmpty()) {
                throw new IllegalArgumentException("File name is empty.");
            }

            // Ensure the upload directory exists
            java.io.File uploadDirFile = new java.io.File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();  // Create directory if it doesn't exist
            }

            String filePath = uploadDir + fileName;
            
            // Create file and save it
            file.transferTo(new java.io.File(filePath));

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Endpoint for ranking a resume against a job posting
    @GetMapping("/rank/{resumeId}/{jobPostingId}")
    public double rankResume(@PathVariable Long resumeId, @PathVariable Long jobPostingId) {
        // Retrieve resume and job posting by IDs
        Resume resume = resumeService.getResumeById(resumeId);
        JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);
        
        if (resume == null || jobPosting == null) {
            throw new ResourceNotFoundException("Resume or Job Posting not found");
        }

        // Match resume with the job posting and return the rank (score)
        return resumeJobMatcher.matchResumeToJob(resume, jobPosting);
    }

    // Endpoint to get a list of all resumes
    @GetMapping("/all")
    public List<Resume> getAllResumes() {
        return resumeService.getAllResumes();
    }

    // Endpoint to get a specific job posting by ID
    @GetMapping("/jobpostings/{id}")
    public JobPosting getJobPosting(@PathVariable Long id) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id);
        
        if (jobPosting == null) {
            throw new ResourceNotFoundException("Job Posting not found");
        }

        return jobPosting;
    }
}
