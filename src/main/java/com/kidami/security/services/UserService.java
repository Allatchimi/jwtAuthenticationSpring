package com.kidami.security.services;

import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserSaveDTO;
import com.kidami.security.dto.UserUpdateDTO;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User registerNewUser(RegisterDTO registerDTO);
    User findByEmail(String email);
    User addRolesToUser(String email, Set<Role> rolesToAdd);

    List<UserDTO> getAllUsers();

    String updateUser(UserUpdateDTO userUpdateDTO);

    boolean deleteUser(int id);
    String deleteUsers( Map<String, Integer> request);
}
