package com.kidami.security.services;

public interface JwtService {
    String generateToken(String email);
    String extractEmail(String token);
    boolean isTokenValid(String token, String userEmail);

}
