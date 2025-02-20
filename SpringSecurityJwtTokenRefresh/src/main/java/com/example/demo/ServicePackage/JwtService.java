package com.example.demo.ServicePackage;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	String extractUserName(String token);
	
	String generateToken (UserDetails userDetails);
	
	boolean isTokenValid(String token, UserDetails userDetails);
	String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);
}
