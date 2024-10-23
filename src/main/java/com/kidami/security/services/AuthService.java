package com.kidami.security.services;

import com.kidami.security.dto.AuthResponseDto;
import com.kidami.security.dto.LoginDTO;
import com.kidami.security.dto.RefreshTokenRequest;
import com.kidami.security.dto.RegisterDTO;

public interface AuthService {
    AuthResponseDto login(LoginDTO loginDTO);

    AuthResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest);

   // String register(RegisterDTO registerDTO);
}
