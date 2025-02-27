package com.example.Sample.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Sample.dto.ResumeEvaluation;

public interface ResumeEvaluationRepo extends JpaRepository<ResumeEvaluation, Long> {

}
