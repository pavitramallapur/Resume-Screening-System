package com.example.Sample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.Sample.config.ResponseStructure;

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
}
