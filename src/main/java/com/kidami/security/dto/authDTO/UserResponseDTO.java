package com.kidami.security.dto.authDTO;

import com.kidami.security.models.AuthProvider;
import com.kidami.security.models.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private AuthProvider provider;
   // private String providerId;
    private String profileImageUrl;
    private boolean emailVerified;
    private Set<Role> roles;
    // PAS DE MOT DE PASSE !
}