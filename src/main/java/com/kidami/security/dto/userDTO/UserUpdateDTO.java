package com.kidami.security.dto.userDTO;

import com.kidami.security.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateDTO {
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;
    @Size(min = 2, max = 30, message = "Le prénom doit contenir entre 2 et 30 caractères")
    private String firstName;
    @Size(min = 2, max = 30, message = "Le nom de famille doit contenir entre 2 et 30 caractères")
    private String lastName;
    @Email(message = "L'email doit être valide")
    private String email;
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    private String profileImageUrl;
    private Boolean emailVerified;
    private Set<Role> roles;
}