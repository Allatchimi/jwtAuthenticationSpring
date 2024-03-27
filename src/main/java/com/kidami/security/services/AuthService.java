package com.kidami.security.services;

import com.kidami.security.dto.LoginDTO;
import com.kidami.security.dto.RegisterDTO;

public interface AuthService {
    LoginDTO login(LoginDTO loginDTO);

    String register(RegisterDTO registerDTO);
}
