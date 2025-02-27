package com.example.Sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.dto.Resume;
import com.example.Sample.service.ResumeService;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public String uploadResume(@RequestParam String candidateName,
                               @RequestParam String email,
                               @RequestParam String jobTitle,
                               @RequestParam Double aiScore,
                               @RequestParam String status,
                               @RequestParam String extractedText,
                               @RequestParam("file") MultipartFile file) {

        // Save the file to a specific directory (or process it)
        String filePath = saveUploadedFile(file);

        // Save resume data to the database
        Resume savedResume = resumeService.saveResume(candidateName, email, jobTitle, aiScore, filePath, status, extractedText);
        
        return "Resume uploaded successfully with ID: " + savedResume.getId();
    }

    // Method to save uploaded file and return its file path
    private String saveUploadedFile(MultipartFile file) {
        try {
            String uploadDir = "/home/pavitra/Downloads";  // Replace this with a valid path
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

}
