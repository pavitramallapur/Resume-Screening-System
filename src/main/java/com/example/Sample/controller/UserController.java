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
	
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<User>> register(@RequestBody User user){
		User user1=userService.register(user);
//		return ResponseEntity.ok(user1);
		ResponseStructure<User>responseStructure=new ResponseStructure<>();
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("User Register succuessfully");
		responseStructure.setData(user1);
//		 return ResponseEntity.status(HttpStatus.CREATED).body(user1);
		return new ResponseEntity<ResponseStructure<User>>(responseStructure,HttpStatus.CREATED);
	
	}
	
	@PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        Optional<User> user = userService.findByEmail(request.getEmail());

        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials!"));
        }

        String token = jwtUtil.generateToken(user.get().getEmail(),user.get().getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
	    User user = userService.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    return ResponseEntity.ok(user);
	
}

	 @PutMapping("/{id}")
	 public ResponseEntity<User> updateProfile(@PathVariable Long id,@RequestBody User user){
		 User user1=userService.updateUser(id, user);
		 return ResponseEntity.ok(user);
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
	    public ResponseEntity<?> refreshToken(@RequestBody Map<String, Object> request) {
	        try {
	            // Get the token from the request - it could be a nested object
	            Object refreshTokenObj = request.get("refreshToken");
	            
	            if (refreshTokenObj == null) {
	                return ResponseEntity.badRequest().body("Refresh token is required");
	            }
	            
	            // Convert to string - we'll clean it in the JwtUtil class
	            String refreshToken = refreshTokenObj.toString();
	            logger.debug("Received refresh token: " + refreshToken);
	            
	            String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
	            
	            Map<String, String> response = new HashMap<>();
	            response.put("accessToken", newAccessToken);
	            
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            logger.error("Error refreshing token: " + e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token: " + e.getMessage());
	        }
	    }


}
