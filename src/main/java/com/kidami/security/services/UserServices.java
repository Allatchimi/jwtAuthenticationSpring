package com.kidami.security.services;

import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserSaveDTO;
import com.kidami.security.dto.UserUpdateDTO;

import java.util.List;

public interface UserServices {
    String addUser(UserSaveDTO userSaveDTO);
    List<UserDTO> getAllUsers();
    String updateUser(UserUpdateDTO userUpdateDTO);
    boolean deleteUser(int id);
}
