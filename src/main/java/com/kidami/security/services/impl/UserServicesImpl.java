package com.kidami.security.services.impl;

import com.kidami.security.dto.authDTO.RegisterDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.dto.userDTO.UserUpdateDTO;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.UserMapper;
import com.kidami.security.models.AuthProvider;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserService {

    private final  static Logger log = LogManager.getLogger(UserServicesImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    UserServicesImpl(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO registerNewUser(RegisterDTO registerDTO) {
        log.debug("Tentative de creation de user {}", registerDTO);
        if(registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'Email du user est obligatoire");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            log.warn("Tentative de création d'un user en double: {}", registerDTO.getEmail());
            throw new DuplicateResourceException("User", "email", registerDTO.getEmail());
        }
        try {
            User user = userMapper.registerDTOToUser(registerDTO);
            String hashedPassword = passwordEncoder.encode(registerDTO.getPassword());
            user.setPassword(hashedPassword);
            user.setProvider(registerDTO.getProvider());
            if (registerDTO.getProvider() == AuthProvider.LOCAL) {
                user.setProviderId(null);
                user.setEmailVerified(false);
            } else {
                user.setProviderId(registerDTO.getProviderId());
                user.setEmailVerified(true);
            }
            Set<Role> defaultRoles = new HashSet<>();
            if(registerDTO.getRoles() != null) {
                defaultRoles.addAll(registerDTO.getRoles());
                user.setRoles(defaultRoles);
            }else {
                defaultRoles.add(Role.STUDENT);
                user.setRoles(defaultRoles);
            }

            User userSaved = userRepository.save(user);
            log.info("Créé avec succès : {}", userSaved.getEmail());

            return userMapper.userToUserDTO(userSaved);
        } catch (DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du user: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création du user", e);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User addRolesToUser(String email, Set<Role> rolesToAdd) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Éviter les doublons
        Set<Role> userRoles = new HashSet<>(user.getRoles());
        userRoles.addAll(rolesToAdd);
        user.setRoles(userRoles);

        return userRepository.save(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::userToUserDTO) // Convert each User to UserDTO
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
    public UserDTO updateUser(UserUpdateDTO userUpdateDTO) {
        log.debug("mise a jour de user {}", userUpdateDTO.getEmail());
        //String hashedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
        User user = userRepository.findByEmail(userUpdateDTO.getEmail())
                .orElseThrow(() ->{
                    log.warn("user n existe pas : {}", userUpdateDTO.getEmail());
                    return  new ResourceNotFoundException("User", "email", userUpdateDTO.getEmail());
                });

        log.trace("Données de mise à jour valides: {}", userUpdateDTO);
        try {
            if (userUpdateDTO.getName() != null) user.setName(userUpdateDTO.getName());
            if (userUpdateDTO.getEmail() != null) user.setEmail(userUpdateDTO.getEmail());

            if (userUpdateDTO.getPassword() != null) {
                String hashedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
                user.setPassword(hashedPassword);
            }

            if (userUpdateDTO.getProvider() != null) {
                // CORRECTION ICI : Pas besoin de toUpperCase() sur un enum
                user.setProvider(userUpdateDTO.getProvider());
            }

            if (userUpdateDTO.getRoles() != null) user.setRoles(userUpdateDTO.getRoles());


             User userUpdated = userRepository.save(user);
            log.info("le user a ete bien mise a jour : {}", userUpdated);
            return userMapper.userToUserDTO(userUpdated);
        }catch (Exception e) {
            log.error("Erreur lors de la mise a jour du user: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true; // User was deleted
        }
        return false; // User not found
    }

    @Override
    public String deleteUsers( Map<String, Long> request) {
        Long id = request.get("id");
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "ok! "; // User was deleted
        }
        return "user not found!";
    }

}
