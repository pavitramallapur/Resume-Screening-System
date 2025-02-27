package com.example.Sample.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.dto.Resume;
import com.example.Sample.repo.ResumeRepo;
@Service
public class ResumeServiceImpl implements ResumeService {
	
	@Autowired
	private ResumeRepo resumeRepo;

	 public Resume saveResume(String candidateName, String email, String jobTitle, Double aiScore, String filePath, String status, String extractedText) {
	        Resume resume = new Resume(candidateName, email, jobTitle, aiScore, filePath, status, extractedText, LocalDateTime.now());
	        return resumeRepo.save(resume);
	    }
}
