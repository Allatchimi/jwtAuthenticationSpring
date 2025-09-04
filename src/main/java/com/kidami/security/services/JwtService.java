package com.kidami.security.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
   // String generateToken(String email);
    String generateToken(Authentication authentication);
    String generateRefreshToken(Authentication authentication);
    String extractEmail(String token);
    boolean isTokenValid(String token, String userEmail);
}

