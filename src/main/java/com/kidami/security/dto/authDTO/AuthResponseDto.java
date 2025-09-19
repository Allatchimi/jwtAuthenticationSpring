package com.kidami.security.dto.authDTO;

import com.kidami.security.dto.userDTO.UserDTO;
import lombok.Data;

import java.time.Instant;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private UserDTO user;
    private Instant expiresAt;

    public static AuthResponseDto fromTokens(String accessToken, String refreshToken,
                                             UserDTO user, Instant expiresAt) {
        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUser(user);
        response.setExpiresAt(expiresAt);
        return response;
    }
}