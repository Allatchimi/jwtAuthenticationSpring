package com.kidami.security.services;

import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.dto.userDTO.UserUpdateDTO;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    UserDTO registerNewUser(RegisterDTO registerDTO);
    User findByEmail(String email);
    User addRolesToUser(String email, Set<Role> rolesToAdd);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(UserUpdateDTO userUpdateDTO);
    boolean deleteUser(int id);
    String deleteUsers( Map<String, Integer> request);
}
