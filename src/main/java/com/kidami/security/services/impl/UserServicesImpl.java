package com.kidami.security.services.impl;

import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserUpdateDTO;
import com.kidami.security.models.Role; // Importer l'énumération Role
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserService {
    private final  static Logger log = LogManager.getLogger(UserServicesImpl.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    // Enregistrer un nouvel utilisateur avec un rôle par défaut
    @Override
    public User registerNewUser(RegisterDTO registerDTO) {
        String hashedPassword = passwordEncoder.encode(registerDTO.getPassword());
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setName(registerDTO.getName());
        user.setPassword(hashedPassword);
        // Ajouter un rôle par défaut à l'utilisateur (par exemple, ROLE_USER)
        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.USER); // Supposons que vous ayez un rôle USER dans l'énumération
        user.setRoles(defaultRoles);
       // log.info("user role registered " + defaultRoles);
        user.setProvider("LOCAL");
        return userRepository.save(user);
    }

    // Trouver un utilisateur par email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Ajouter un ou plusieurs rôles à un utilisateur existant
    @Override
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

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserDTO) // Convert each User to UserDTO
                .collect(Collectors.toList());
    }
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoles(user.getRoles());
        // Set other properties as needed
        return userDTO;
    }

    @Override
    public String updateUser(UserUpdateDTO userUpdateDTO) {
        String hashedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
        User user = userRepository.findByEmail(userUpdateDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userUpdateDTO.getName());
        user.setEmail(userUpdateDTO.getEmail());
        user.setProvider(userUpdateDTO.getProvider());
        user.setRoles(userUpdateDTO.getRoles());
        user.setPassword(hashedPassword);
        // Update other fields as necessary

        userRepository.save(user);
        return "User updated successfully!";
    }

    @Override
    public boolean deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true; // User was deleted
        }
        return false; // User not found
    }

    @Override
    public String deleteUsers( Map<String, Integer> request) {
        int id = request.get("id");
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "ok! "; // User was deleted
        }
        return "user not found!";
    }

}
