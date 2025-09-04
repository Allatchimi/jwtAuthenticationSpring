package com.kidami.security.services;


import com.kidami.security.dto.authDTO.RegisterDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.dto.userDTO.UserUpdateDTO;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    UserDTO registerNewUser(RegisterDTO registerDTO);
    UserDTO updateUser(UserUpdateDTO userUpdateDTO);
    boolean deleteUser(Long id);
    List<UserDTO> getAllUsers();
    User findByEmail(String email);
    User addRolesToUser(String email, Set<Role> rolesToAdd);
    String deleteUsers( Map<String, Long> request);
}
