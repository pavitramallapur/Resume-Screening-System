package com.example.Sample.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.config.ResponseStructure;
import com.example.Sample.dto.Resume;

public interface ResumeService {

//	String processResume(MultipartFile file);

	Resume saveResume(String candidateName, String email, String jobTitle,  String filePath,
			String status, String extractedText);

	List<Resume> getAllResumes();

	Resume getResumeById(Long resumeId);

	Resume saveResume(Resume resume);

}
