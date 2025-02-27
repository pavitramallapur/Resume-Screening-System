package com.example.Sample.service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Sample.dto.JobPosting;
import com.example.Sample.dto.Resume;
import com.example.Sample.util.ResumeJobMatcher;

@Service
public class ResumeProcessingService {
    
    private final ResumeJobMatcher resumeJobMatcher;
    private final ExecutorService executorService;

    public ResumeProcessingService(ResumeJobMatcher resumeJobMatcher) {
        this.resumeJobMatcher = resumeJobMatcher;
        this.executorService = Executors.newFixedThreadPool(10); 
    }

    public List<Double> processResumesParallel(List<Resume> resumes, JobPosting jobPosting) {
        List<Future<Double>> futures = resumes.stream()
                .map(resume -> executorService.submit(() -> resumeJobMatcher.matchResumeToJob(resume, jobPosting)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();  // Retrieve AI Score
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return 0.0;
                    }
                })
                .collect(Collectors.toList());
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
