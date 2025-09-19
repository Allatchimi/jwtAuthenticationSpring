package com.kidami.security.dto.userDTO;

import com.kidami.security.enums.AuthProvider;
import com.kidami.security.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImageUrl;
    private boolean emailVerified;
    private Set<Role> roles;
    private AuthProvider provider;
    private String providerId;

}
