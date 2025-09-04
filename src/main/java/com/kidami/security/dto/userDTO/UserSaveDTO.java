package com.kidami.security.dto.userDTO;

import com.kidami.security.models.AuthProvider;
import com.kidami.security.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSaveDTO {
    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;
    @Size(min = 2, max = 30, message = "Le prénom doit contenir entre 2 et 30 caractères")
    private String firstName;
    @Size(min = 2, max = 30, message = "Le nom de famille doit contenir entre 2 et 30 caractères")
    private String lastName;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    @NotNull(message = "Le provider est obligatoire")
    private AuthProvider provider;
    private String providerId; // Pour les utilisateurs OAuth2
    private String profileImageUrl; // URL de l'avatar
    private boolean emailVerified = false; // Vérification d'email
    private Set<Role> roles = new HashSet<>();

}
