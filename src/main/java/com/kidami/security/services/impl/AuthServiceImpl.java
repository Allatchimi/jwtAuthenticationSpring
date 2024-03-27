package com.kidami.security.services.impl;

import com.kidami.security.dto.LoginDTO;
import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.exception.BlogAPIException;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public LoginDTO login(LoginDTO loginDTO) {

        LoginDTO login = new LoginDTO(loginDTO.getUsernameOrEmail(),loginDTO.getPassword());

        return login;
    }

    @Override
    public String register(RegisterDTO registerDTO) {
        // add check for username exists in database
        if(userRepository.existsByFirstname(registerDTO.getFirstname())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        // add check for email exists in database
        if(userRepository.existsByEmail(registerDTO.getEmail())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }


        User user = new User();
        user.setFirstname(registerDTO.getFirstname());
        user.setLastname(registerDTO.getLastname());
        user.setEmail(registerDTO.getEmail());
        user.setRole(registerDTO.getRole());
        user.setPassword(registerDTO.getPassword());
        //user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        userRepository.save(user);

        return "User registered successfully!.";
    }
}
