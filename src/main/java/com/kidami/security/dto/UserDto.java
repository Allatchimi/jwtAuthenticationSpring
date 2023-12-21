package com.kidami.security.dto;

import com.kidami.security.models.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;

}
