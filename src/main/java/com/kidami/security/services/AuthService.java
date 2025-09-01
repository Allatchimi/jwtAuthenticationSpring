package com.kidami.security.services;

import com.kidami.security.dto.AuthResponseDto;
import com.kidami.security.dto.LoginDTO;
import com.kidami.security.dto.RefreshTokenRequest;

import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.User;

import java.time.Instant;

public interface AuthService {
    AuthResponseDto login(LoginDTO loginDTO);

    AuthResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest);
    void deleteRefreshTokenForUser(User user);
    RefreshToken createOrUpdateRefreshToken(User user, String newToken, Instant newExpiryDate);

    // String register(RegisterDTO registerDTO);
}
