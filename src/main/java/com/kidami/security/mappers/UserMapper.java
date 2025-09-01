package com.kidami.security.mappers;

import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.dto.UserDTO;
import com.kidami.security.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToRegisterDTO(User user);

    @Mapping(target = "password", ignore = true) // On ignore le password car il sera hashé dans le service
    @Mapping(target = "roles", ignore = true)    // Les rôles seront gérés dans le service
    @Mapping(target = "provider", ignore = true) // Le provider sera géré dans le service
    User registerDTOToUser(RegisterDTO registerDTO);
}