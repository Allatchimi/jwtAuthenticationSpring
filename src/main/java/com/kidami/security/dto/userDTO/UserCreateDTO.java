package com.kidami.security.dto.userDTO;

import com.kidami.security.enums.AuthProvider;
import com.kidami.security.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDTO {
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

    // Validation conditionnelle selon le provider
    @AssertTrue(message = "Le mot de passe est obligatoire pour l'inscription locale")
    public boolean isPasswordValid() {
        if (provider == AuthProvider.LOCAL) {
            return password != null && !password.trim().isEmpty();
        }
        return true; // Pas de password requis pour OAuth2
    }

    @AssertTrue(message = "Le providerId est obligatoire pour les providers OAuth2")
    public boolean isProviderIdValid() {
        if (provider != null && provider != AuthProvider.LOCAL) {
            return providerId != null && !providerId.trim().isEmpty();
        }
        return true; // Pas de providerId requis pour LOCAL
    }

}
