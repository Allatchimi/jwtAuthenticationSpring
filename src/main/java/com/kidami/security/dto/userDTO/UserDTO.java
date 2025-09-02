package com.kidami.security.dto.userDTO;

import com.kidami.security.models.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;
    private String name;
    @Column( unique = true)
    private String email;
    private String password;
    private String provider;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // ou EnumType.ORDINAL si vous préférez
    private Set<Role> roles = new HashSet<>(); // Utilisation d'un ensemble pour les rôles

}
