package com.example.Sample.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Sample.dto.Resume;

public interface ResumeRepo extends JpaRepository<Resume, Long>{

}
