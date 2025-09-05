package com.kidami.security.mappers;


import com.kidami.security.dto.authDTO.RegisterDTO;
import com.kidami.security.dto.authDTO.UserResponseDTO;
import com.kidami.security.dto.userDTO.UserDTO;

import com.kidami.security.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Mapping de RegisterDTO vers User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    User registerDTOToUser(RegisterDTO registerDTO);

    // Mapping de User vers UserDTO
    UserDTO userToUserDTO(User user);

    // Mapping de User vers UserResponseDTO
    UserResponseDTO userToUserResponseDTO(User user);

    // Mapping de UserDTO vers User (si nécessaire)
    User userDTOToUser(UserDTO userDTO);
}