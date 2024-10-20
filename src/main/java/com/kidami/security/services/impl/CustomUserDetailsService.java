package com.kidami.security.services.impl;

import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Récupérer l'utilisateur par email
        User userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Récupérer les rôles et les convertir en GrantedAuthority
        // Supposons que getRole() retourne une chaîne; vous pouvez adapter cette partie selon votre modèle
        String role = userEntity.getRole(); // Assurez-vous que c'est une chaîne ou une collection
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        // Créer et retourner un UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(Collections.singletonList(authority)) // Ajoutez ici les rôles
                .accountLocked(false) // Définissez si le compte est verrouillé
                .credentialsExpired(false) // Définissez si les identifiants sont expirés
                .disabled(false) // Définissez si le compte est désactivé
                .build();
    }
}
