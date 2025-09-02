package com.kidami.security.mappers;

import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToRegisterDTO(User user);
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "provider", ignore = true)
    User registerDTOToUser(RegisterDTO registerDTO);
}