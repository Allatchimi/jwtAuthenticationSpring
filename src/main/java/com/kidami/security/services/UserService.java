package com.kidami.security.services;

import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserSaveDTO;
import com.kidami.security.dto.UserUpdateDTO;
import com.kidami.security.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerNewUser(String email, String password);
    Optional<User> findByEmail(String email);
}
