package com.example.Sample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.Sample.config.ResponseStructure;
import com.example.Sample.dto.JobPosting;

@RestControllerAdvice
public class ApplicationHandler {

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ResponseStructure<String>> handleInvalidRoleException(InvalidRoleException ex) {
        ResponseStructure<String> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
        responseStructure.setMessage(ex.getMessage());
        responseStructure.setData("Role must be one of: ADMIN, HR, CANDIDATE");

        return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ResponseStructure<JobPosting>> handleIdNotFoundException(IdNotFoundException ex) {
        ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
        responseStructure.setMessage(ex.getMessage());
        responseStructure.setData(null);  // Set data to null instead of a string message

        return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
    }
}
