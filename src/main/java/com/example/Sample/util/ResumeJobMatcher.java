package com.example.Sample.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.Sample.dto.JobPosting;
import com.example.Sample.dto.Resume;

@Component
public class ResumeJobMatcher {

    // Calculate AI Score for Resume based on Job Description
    public double matchResumeToJob(Resume resume, JobPosting jobPosting) {
        // Tokenize both resume text and job description
        List<String> resumeTokens = TextProcessor.tokenize(resume.getExtractedText());
        List<String> jobDescriptionTokens = TextProcessor.tokenize(jobPosting.getDescription());
        List<String> requiredSkillsTokens = TextProcessor.tokenize(jobPosting.getRequiredSkills());

        // Calculate cosine similarity
        double descriptionMatch = TextProcessor.cosineSimilarity(resumeTokens, jobDescriptionTokens);
        double skillsMatch = TextProcessor.cosineSimilarity(resumeTokens, requiredSkillsTokens);

        // AI score could be a weighted sum of both matches
        double aiScore = descriptionMatch * 0.7 + skillsMatch * 0.3;

        // Return AI score as a percentage
        return aiScore * 100;  // Multiply by 100 to convert to percentage
    }
}
