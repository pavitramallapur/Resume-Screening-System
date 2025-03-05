package com.example.Sample.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.Sample.config.ResponseStructure;
import com.example.Sample.dto.User;

public interface UserService {

	User register(User user);

	Optional<User> findByEmail(String email);

	Optional<User> findById(Long id);

//	User updateUser(Long id);

	User updateUser(Long id, User u1);

	boolean existsByEmail(String email);

	ResponseEntity<ResponseStructure<List<User>>> findAll();

	ResponseEntity<ResponseStructure<User>> deleteUser(Long id);

//	ResponseEntity<User> findById(User id);

}
