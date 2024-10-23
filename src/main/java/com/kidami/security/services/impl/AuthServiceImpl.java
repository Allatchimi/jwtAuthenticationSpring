package com.kidami.security.services.impl;

import com.kidami.security.dto.AuthResponseDto;
import com.kidami.security.dto.LoginDTO;
import com.kidami.security.dto.RefreshTokenRequest;
import com.kidami.security.dto.RegisterDTO;
import com.kidami.security.exception.BlogAPIException;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponseDto login(LoginDTO loginDTO) {
        // 01 - AuthenticationManager is used to authenticate the user
        // Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        /* 02 - SecurityContextHolder is used to allows the rest of the application to know
        that the user is authenticated and can use user data from Authentication object */
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 03 - Generate the token based on username and secret key
        var user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()-> new IllegalArgumentException("invalid email or password"));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        // Sauvegarder le refresh token en base de données
       // user.setRefreshToken(refreshToken);
       // userRepository.save(user);

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);
        authResponseDto.setRefreshToken(refreshToken);

        return authResponseDto;
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractEmail(refreshTokenRequest.getToken());

        // Vérifier si l'utilisateur existe dans la base de données
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new RuntimeException("User not found"));

        // Vérifier la validité du refresh token
        if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user.getEmail())) {


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            org.springframework.security.core.userdetails.User securityUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            // Générer un nouveau token JWT pour l'utilisateur
            String newAccessToken = jwtService.generateToken((Authentication) securityUser); // Ici, vous pouvez inclure les rôles

            // Facultatif : Vous pouvez générer un nouveau refreshToken si nécessaire.
            String newRefreshToken = jwtService.generateRefreshToken(authentication);

            // Stocker le nouveau refresh token en base de données (facultatif)
           // user.setRefreshToken(newRefreshToken);
           // userRepository.save(user);

            // Créer la réponse contenant le nouveau token JWT et le refreshToken
            AuthResponseDto authResponseDto = new AuthResponseDto();
            authResponseDto.setAccessToken(newAccessToken);
            authResponseDto.setRefreshToken(newRefreshToken); // Si vous générez un nouveau refresh token

            return authResponseDto;
        }

        throw new RuntimeException("Invalid refresh token");
    }

/*
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
    }*/
}
