package com.example.Sample.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Sample.dto.User;
import com.example.Sample.repo.UserRepo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Override
//	public User register(User user) {
//		User user1=userRepo.save(user);
//		System.out.println(user1);
//		return user;
//	}

	@Override
	public User register(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepo.findById(id);
	}

	@Override
	public User updateUser(Long id, User u1) {

		Optional<User> userOptional = userRepo.findById(id);

		if (userOptional.isPresent()) {
			User user = userOptional.get();

			user.setName(u1.getName());
			user.setEmail(u1.getEmail());
			user.setPassword(u1.getPassword());
			user.setRole(u1.getRole());

			return userRepo.save(user);
		} else {

			throw new RuntimeException("User not found with id: " + id);
		}
	}

	@Override
	public boolean existsByEmail(String email) {
	    return userRepo.existsByEmail(email);
	}


}
