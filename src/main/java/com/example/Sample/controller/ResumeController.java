package com.example.Sample.controller;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Sample.config.ResponseStructure;
import com.example.Sample.dto.InterviewDetails;
import com.example.Sample.dto.JobPosting;
import com.example.Sample.dto.Resume;
import com.example.Sample.service.InterviewSchedulerProducer;
import com.example.Sample.service.JobPostingService;
import com.example.Sample.service.ResumeService;
import com.example.Sample.util.ResumeJobMatcher;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

	@Autowired
	private InterviewSchedulerProducer interviewSchedulerProducer;

	private final ExecutorService executorService = Executors.newFixedThreadPool(5);

	@PostMapping("/upload")
	public ResponseEntity<ResponseStructure<String>> uploadResume(@RequestParam String candidateName,
			@RequestParam String email, @RequestParam String jobTitle, @RequestParam String status,
			@RequestParam("file") MultipartFile file) throws IOException {

		ResponseStructure<String> responseStructure = new ResponseStructure<>();

		// Check if the file is null or empty
		if (file == null || file.isEmpty()) {
			responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
			responseStructure.setMessage("File is required and cannot be empty");
			responseStructure.setData(null);
			return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
		}

		// Process the resume
		String resultMessage = processResume(candidateName, email, jobTitle, status, file);

		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage(resultMessage);
		responseStructure.setData("Resume uploaded for: " + candidateName);

		return new ResponseEntity<>(responseStructure, HttpStatus.OK);
	}

	@PostMapping("/uploadMultiple")
	public List<String> uploadMultipleResumes(@RequestParam String candidateName, @RequestParam String email,
			@RequestParam String jobTitle, @RequestParam String status, @RequestParam("files") MultipartFile[] files) {
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

	private String processResume(String candidateName, String email, String jobTitle, String status,
			MultipartFile file) {
		try {
			String filePath = saveUploadedFile(file);
			String extractedText = extractTextFromPDF(filePath);
			Resume savedResume = resumeService.saveResume(candidateName, email, jobTitle, filePath, status,
					extractedText);
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

	@GetMapping("/rank/{resumeId}/{jobPostingId}")
	public String rankResume(@PathVariable Long resumeId, @PathVariable Long jobPostingId) {
	    Resume resume = resumeService.getResumeById(resumeId);
	    JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);

	    if (resume == null || jobPosting == null) {
	        throw new ResourceNotFoundException("Resume or Job Posting not found");
	    }

	    double aiScore = resumeJobMatcher.matchResumeToJob(resume, jobPosting);
	    System.out.println("AI Score generated: " + aiScore);

	    if (aiScore >= 75) {
	        String interviewDetails = generateInterviewDetails(resume);
	        interviewSchedulerProducer.scheduleInterview(interviewDetails);
	        System.out.println("Interview scheduled for Resume ID: " + resumeId);
	    }
	    return "AI Score:\nResume: " + aiScore + "%" + " \n✅ Interview scheduled for Resume ID: " + resumeId ;
	    
	}

	private String generateInterviewDetails(Resume resume) {

		String candidateName = resume.getCandidateName();
		LocalDate interviewDate = LocalDate.now().plusDays(3);

		return "Candidate: " + candidateName + ", Interview Date: " + interviewDate;
	}

	@GetMapping("/rank/{resumeId1}/{resumeId2}/{jobPostingId}")
	public String rankResumes(@PathVariable Long resumeId1, @PathVariable Long resumeId2,
			@PathVariable Long jobPostingId) throws ExecutionException, InterruptedException {

		// Fetch job posting
		JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId);
		if (jobPosting == null) {
			throw new ResourceNotFoundException("Job Posting not found");
		}

		// Fetch resumes in parallel
		CompletableFuture<Resume> resumeFuture1 = CompletableFuture
				.supplyAsync(() -> resumeService.getResumeById(resumeId1));
		CompletableFuture<Resume> resumeFuture2 = CompletableFuture
				.supplyAsync(() -> resumeService.getResumeById(resumeId2));

		Resume resume1 = resumeFuture1.get();
		Resume resume2 = resumeFuture2.get();

		if (resume1 == null || resume2 == null) {
			throw new ResourceNotFoundException("One or both resumes not found");
		}

		// Compute AI scores in parallel
		CompletableFuture<Double> scoreFuture1 = CompletableFuture
				.supplyAsync(() -> resumeJobMatcher.matchResumeToJob(resume1, jobPosting));
		CompletableFuture<Double> scoreFuture2 = CompletableFuture
				.supplyAsync(() -> resumeJobMatcher.matchResumeToJob(resume2, jobPosting));

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
