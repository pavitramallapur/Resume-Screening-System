package com.example.Sample.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.dto.Resume;

public interface ResumeService {

//	String processResume(MultipartFile file);

	Resume saveResume(String candidateName, String email, String jobTitle, Double aiScore, String filePath,
			String status, String extractedText);

}
