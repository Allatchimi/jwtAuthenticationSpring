package com.kidami.security.dto.authDTO;

import lombok.Data;

import java.time.Instant;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDTO user;
    private Instant expiresAt;

    // Optionnel : méthode utilitaire
    public static AuthResponseDto fromTokens(String accessToken, String refreshToken,
                                             UserResponseDTO user, Instant expiresAt) {
        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUser(user);
        response.setExpiresAt(expiresAt);
        return response;// ✅ Utilise le DTO sécurisé
    }
}