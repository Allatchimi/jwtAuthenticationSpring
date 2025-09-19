package com.kidami.security.services;
import com.kidami.security.dto.authDTO.*;
import com.kidami.security.dto.authDTO.LoginDTO;

import com.kidami.security.dto.userDTO.UserCreateDTO;
import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

public interface AuthService {
        // Connexion
    AuthResponseDto firebaseLogin(String idToken, HttpServletRequest request);    // Firebase login
    AuthResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest); // Refresh token
    AuthResponseDto login(LoginDTO loginDTO, HttpServletRequest request);
    AuthResponseDto registerAndAuthenticate(UserCreateDTO userCreateDTO, HttpServletRequest request);

    RefreshToken createOrUpdateRefreshToken(User user, String newToken, Instant newExpiryDate,
                                            HttpServletRequest request);

    void deleteRefreshTokenForUser(User user);
    void logout(User user);

}
