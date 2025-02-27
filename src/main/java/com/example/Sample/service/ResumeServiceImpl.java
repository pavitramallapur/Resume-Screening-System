package com.example.Sample.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.dto.Resume;
import com.example.Sample.repo.ResumeRepo;
@Service
public class ResumeServiceImpl implements ResumeService {
	
	@Autowired
	private ResumeRepo resumeRepo;

//	 public Resume saveResume(String candidateName, String email, String jobTitle, Double aiScore, String filePath, String status, String extractedText) {
//	        Resume resume = new Resume(candidateName, email, jobTitle, aiScore, filePath, status, extractedText, LocalDateTime.now());
//	        return resumeRepo.save(resume);
//	    }
	 @Override
	    public Resume saveResume(String candidateName, String email, String jobTitle,  String filePath, String status, String extractedText) {
	        // Create a new Resume object
	        Resume resume = new Resume(candidateName, email, jobTitle, filePath, status, extractedText, LocalDateTime.now());
	        
	        // Save the resume to the repository
	        return resumeRepo.save(resume);
	    }

	    // Get all resumes from the repository
	    @Override
	    public List<Resume> getAllResumes() {
	        return resumeRepo.findAll();
	    }

	    // Get a specific resume by its ID
	    public Resume getResumeById(Long resumeId) {
	        return resumeRepo.findById(resumeId).orElseThrow(() -> 
	            new ResourceNotFoundException("Resume not found with ID: " + resumeId));
	    }
	


	    // Save or update a resume based on the provided Resume object
	    @Override
	    public Resume saveResume(Resume resume) {
	        // You can apply any additional logic here if needed before saving
	        resume.setUploadedAt(LocalDateTime.now()); // Set the last updated time
	        return resumeRepo.save(resume);
	    }
	}

