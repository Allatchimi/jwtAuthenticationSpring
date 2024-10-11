package com.kidami.security.dto;

import com.kidami.security.models.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
   // @Enumerated(EnumType.STRING)
    private String role;
}
