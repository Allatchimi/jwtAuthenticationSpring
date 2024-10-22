package com.kidami.security.services.impl;

import com.kidami.security.models.Role; // Importer l'énumération Role
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServicesImpl implements UserService {
    private final  static Logger log = LogManager.getLogger(UserServicesImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Enregistrer un nouvel utilisateur avec un rôle par défaut
    public User registerNewUser(String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        // Ajouter un rôle par défaut à l'utilisateur (par exemple, ROLE_USER)
        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.USER); // Supposons que vous ayez un rôle USER dans l'énumération
        user.setRoles(defaultRoles);

        log.info("user role registered " + defaultRoles);
        user.setProvider("LOCAL");
        return userRepository.save(user);
    }

    // Trouver un utilisateur par email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Ajouter un ou plusieurs rôles à un utilisateur existant
    public User addRolesToUser(String email, Set<Role> rolesToAdd) {
        User user = findByEmail(email);
        if (user != null) {
            Set<Role> userRoles = user.getRoles(); // Récupérer les rôles actuels de l'utilisateur
            userRoles.addAll(rolesToAdd); // Ajouter les nouveaux rôles
            user.setRoles(userRoles); // Mettre à jour les rôles de l'utilisateur

            return userRepository.save(user); // Enregistrer les modifications dans la base de données
        }
        return null; // Retourner null si l'utilisateur n'existe pas
    }
}
