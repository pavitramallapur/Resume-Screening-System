package com.example.Sample.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Sample.config.ResponseStructure;
import com.example.Sample.dto.AuthRequest;
import com.example.Sample.dto.User;
import com.example.Sample.dto.User.Role;
import com.example.Sample.exception.InvalidRoleException;
import com.example.Sample.service.UserService;
import com.example.Sample.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

//	@PostMapping("/register")
//	public ResponseEntity<ResponseStructure<User>> register(@RequestBody User user){
//		User user1=userService.register(user);
////		return ResponseEntity.ok(user1);
//		ResponseStructure<User>responseStructure=new ResponseStructure<>();
//		responseStructure.setStatus(HttpStatus.CREATED.value());
//		responseStructure.setMessage("User Register succuessfully");
//		responseStructure.setData(user1);
////		 return ResponseEntity.status(HttpStatus.CREATED).body(user1);
//		return new ResponseEntity<ResponseStructure<User>>(responseStructure,HttpStatus.CREATED);
//	
//	}
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<User>> register(@RequestBody User user) {
		try {
			Role role;
			try {
				role = Role.valueOf(user.getRole().toString().toUpperCase());
				user.setRole(role);
			} catch (IllegalArgumentException e) {
				throw new InvalidRoleException(
						"Invalid role: " + user.getRole() + ". Allowed values: ADMIN, HR, CANDIDATE");
			}

			// Register user
			User savedUser = userService.register(user);

			// Response
			ResponseStructure<User> responseStructure = new ResponseStructure<>();
			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setMessage("User registered successfully");
			responseStructure.setData(savedUser);

			return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
		} catch (InvalidRoleException e) {
			throw e; 
		}
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Map<String, String>>> login(@RequestBody AuthRequest request) {
		Optional<User> user = userService.findByEmail(request.getEmail());
		ResponseStructure<Map<String, String>> responsestructure = new ResponseStructure<>();

		if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
			responsestructure.setMessage("Invalid credentials!");
			return new ResponseEntity<ResponseStructure<Map<String, String>>>(responsestructure,
					HttpStatus.BAD_REQUEST);
		}

		String token = jwtUtil.generateToken(user.get().getEmail(), user.get().getRole());
		responsestructure.setStatus(HttpStatus.OK.value());
		responsestructure.setMessage("Login successfully");
		responsestructure.setData(token);
		return new ResponseEntity<ResponseStructure<Map<String, String>>>(responsestructure, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<User>> getUserProfile(@PathVariable Long id) {
		User user = userService.findById(id).orElse(null);
		if (user != null) {
			
			ResponseStructure<User> responseStructure = new ResponseStructure<>();
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("User Found Successfully");
			responseStructure.setData(user);
			return new ResponseEntity<ResponseStructure<User>>(responseStructure, HttpStatus.OK);
		} else {
//	    return ResponseEntity.ok(user);
			   ResponseStructure<User> responseStructure = new ResponseStructure<>();
		        responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
		        responseStructure.setMessage("User not found");
		        responseStructure.setData("User with ID " + id + " does not exist.");
		        return new ResponseEntity<ResponseStructure<User>>(responseStructure, HttpStatus.NOT_FOUND);
		    }
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResponseStructure<User>> updateProfile(@PathVariable Long id, @RequestBody User user) {
		User user1 = userService.updateUser(id, user);
		ResponseStructure<User> responseStructure=new ResponseStructure<>();
		responseStructure.setMessage("User Successfully Updated");
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setData(user);
		return new ResponseEntity<ResponseStructure<User>> (responseStructure,HttpStatus.OK);
	}

//	 @PostMapping("/refresh-token")
//	 public ResponseEntity<String> refreshToken(@RequestBody String refreshToken) {
//	     try {
//	         String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
//	         return ResponseEntity.ok(newAccessToken);
//	     } catch (Exception e) {
//	         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
//	     }
//	 }

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/refresh")
	public ResponseEntity<ResponseStructure<Map<String, String>>> refreshToken(@RequestBody Map<String, Object> request) {
	    try {
	        // Get the token from the request - it could be a nested object
	        Object refreshTokenObj = request.get("refreshToken");

	        if (refreshTokenObj == null) {
	            ResponseStructure<Map<String, String>>responseStructure = new ResponseStructure<>();
	            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
	            responseStructure.setMessage("Refresh token is required");
	            responseStructure.setData("No refresh token provided");
	            return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
	        }

	        // Convert to string - we'll clean it in the JwtUtil class
	        String refreshToken = refreshTokenObj.toString();
	        logger.debug("Received refresh token: " + refreshToken);

	        String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);

	        Map<String, String> response = new HashMap<>();
	        response.put("accessToken", newAccessToken);

	        // Successful response with the new access token
	        ResponseStructure<Map<String, String>> responseStructure = new ResponseStructure<>();
	        responseStructure.setStatus(HttpStatus.OK.value());
	        responseStructure.setMessage("Access token refreshed successfully");
	        responseStructure.setData(response);

	        return new ResponseEntity<ResponseStructure<Map<String, String>>>(responseStructure, HttpStatus.OK);

	    } catch (Exception e) {
	        logger.error("Error refreshing token: " + e.getMessage(), e);

	        // Error response when token refresh fails
	        ResponseStructure<Map<String, String>>responseStructure = new ResponseStructure<>();
	        responseStructure.setStatus(HttpStatus.UNAUTHORIZED.value());
	        responseStructure.setMessage("Invalid refresh token: " + e.getMessage());
	        responseStructure.setData("The provided refresh token is invalid or expired.");

	        return new ResponseEntity<ResponseStructure<Map<String, String>>>(responseStructure, HttpStatus.UNAUTHORIZED);
	    }
	}

}
