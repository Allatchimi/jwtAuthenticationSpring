package com.kidami.security.services.impl;

import com.kidami.security.models.Role; // Importer l'énumération Role
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(CustomOAuth2UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Récupérer l'utilisateur par email
        User userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        logger.info("Retrieved OAuth2 user attributes: {}", userEntity);

        // Récupérer les rôles sous forme de Set<Role> et les convertir en GrantedAuthority
        Set<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // Convertir le rôle en chaîne de caractères
                .collect(Collectors.toSet());

        final String email  = userEntity.getEmail();
        final String name  = userEntity.getName();

        // Log specific attributes
        logger.info("User email: {}", email);
        logger.info("User name: {}", name);
        logger.info("User roles: {}", userEntity.getRoles());

        // Log pour vérifier l'utilisateur trouvé
        System.out.println("User found: " + userEntity);

        // Créer et retourner un UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities) // Ajouter ici les rôles
                .accountLocked(false) // Définissez si le compte est verrouillé
                .credentialsExpired(false) // Définissez si les identifiants sont expirés
                .disabled(false) // Définissez si le compte est désactivé
                .build();
    }
}
