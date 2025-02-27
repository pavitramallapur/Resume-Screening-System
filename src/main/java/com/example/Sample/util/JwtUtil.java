package com.example.Sample.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret:your_very_long_and_secure_secret_key_must_be_32_bytes_minimum}")
    private String secretKeyString;
    
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private Key key() {
        if (secretKeyString.length() >= 32) {
            return Keys.hmacShaKeyFor(secretKeyString.getBytes());
        } else {
            logger.warn("JWT secret key is less than 32 bytes. This is not secure for HS256.");
            return Keys.hmacShaKeyFor(secretKeyString.getBytes());
        }
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        token = extractRealToken(token);
        
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); 
    }
    
    public String extractRole(String token) {
        token = extractRealToken(token);
        
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class); 
    }

    public String refreshAccessToken(String refreshToken) {
        refreshToken = extractRealToken(refreshToken);
        logger.debug("Refreshing with token: " + refreshToken);
        
        if (validateToken(refreshToken)) {
            String email = extractEmail(refreshToken); 
            String role = extractRole(refreshToken);   
            return generateToken(email, role); 
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    public boolean validateToken(String token) {
        try {
            token = extractRealToken(token);
            logger.debug("Validating token: " + token);
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            logger.debug("Token Expiration Date: " + claims.getExpiration());
            
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.error("Malformed token: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Token validation failed: " + e.getMessage(), e); 
            return false; 
        }
    }

    /**
     * Extracts the actual JWT token from various potential formats
     */
    private String extractRealToken(String input) {
        if (input == null) {
            logger.error("Null token provided");
            throw new IllegalArgumentException("Token cannot be null");
        }
        
        // Log the original input for debugging
        logger.debug("Original token input: [" + input + "]");
        
        // 1. Look for a JWT pattern (base64url.base64url.base64url)
        Pattern jwtPattern = Pattern.compile("(eyJ[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)");
        Matcher matcher = jwtPattern.matcher(input);
        
        if (matcher.find()) {
            String extractedToken = matcher.group(1);
            logger.debug("Extracted JWT token using regex: " + extractedToken);
            return extractedToken;
        }
        
        // 2. If we can't find a JWT pattern, try removing field name if present
        if (input.contains("refreshToken")) {
            // Extract just the part after the last quote
            Pattern quotePattern = Pattern.compile("\"([^\"]+)\"\\s*$");
            matcher = quotePattern.matcher(input);
            
            if (matcher.find()) {
                String extractedToken = matcher.group(1);
                logger.debug("Extracted token from quotes: " + extractedToken);
                return extractedToken;
            }
        }
        
        // 3. Basic cleaning as a fallback
        input = input.trim();
        if ((input.startsWith("\"") && input.endsWith("\"")) || 
            (input.startsWith("'") && input.endsWith("'"))) {
            input = input.substring(1, input.length() - 1);
        }
        
        logger.debug("Cleaned token: " + input);
        return input;
    }
   
    private void decodeTokenParts(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String headerBase64Url = parts[0];
                String payloadBase64Url = parts[1];

                String header = new String(Base64.getUrlDecoder().decode(headerBase64Url));
                String payload = new String(Base64.getUrlDecoder().decode(payloadBase64Url));

                logger.debug("Decoded Header: " + header);
                logger.debug("Decoded Payload: " + payload);
            } else {
                logger.warn("Token doesn't have 3 parts (header, payload, signature): " + token);
            }
        } catch (Exception e) {
            logger.error("Error decoding token parts: " + e.getMessage(), e);
        }
    }
}