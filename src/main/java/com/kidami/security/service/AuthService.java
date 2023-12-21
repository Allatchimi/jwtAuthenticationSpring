package com.kidami.security.service;

import com.kidami.security.dto.SignUpRequest;
import com.kidami.security.dto.UserDto;

public interface AuthService {
    UserDto createUser(SignUpRequest signUpRequest);
}
