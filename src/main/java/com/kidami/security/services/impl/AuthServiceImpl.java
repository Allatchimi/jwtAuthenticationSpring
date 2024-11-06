package com.kidami.security.services.impl;

import com.kidami.security.dto.AuthResponseDto;
import com.kidami.security.dto.LoginDTO;
import com.kidami.security.dto.RefreshTokenRequest;
import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.User;
import com.kidami.security.repository.RefreshTokenRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponseDto login(LoginDTO loginDTO) {
        // 01 - Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        // 02 - Stocker l'authentification dans le SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 03 - Récupérer l'utilisateur
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // 04 - Générer le token JWT et le refresh token
        String token = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);
        Instant expiryDate = Instant.now().plus(Duration.ofDays(30));

        // Crée ou met à jour le refresh token
        createOrUpdateRefreshToken(user, refreshToken, expiryDate);

        // Créer la réponse
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);
        authResponseDto.setRefreshToken(refreshToken);

        return authResponseDto;
    }

    @Override
    public RefreshToken createOrUpdateRefreshToken(User user, String newToken, Instant newExpiryDate) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {
            // Met à jour l'ancien token
            RefreshToken refreshToken = existingToken.get();
            refreshToken.setToken(newToken);
            refreshToken.setExpiryDate(newExpiryDate);
            return refreshTokenRepository.save(refreshToken);
        } else {
            // Crée un nouveau token
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(newToken);
            refreshToken.setExpiryDate(newExpiryDate);
            refreshToken.setUser(user);
            return refreshTokenRepository.save(refreshToken);
        }
    }

    @Override
    public void deleteRefreshTokenForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // 01 - Extraire l'email à partir du refresh token
        String userEmail = jwtService.extractEmail(refreshTokenRequest.getToken());

        // 02 - Vérifier si l'utilisateur existe dans la base de données
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new RuntimeException("User not found"));

        // 03 - Vérifier la validité du refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No refresh token found for user"));

        if (!jwtService.isTokenValid(refreshTokenRequest.getToken(), user.getEmail())) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 04 - Créer une authentification manuelle pour générer un nouveau token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,  // Les credentials sont null ici car c'est une authentification basée sur le token
                null
        );

        // 05 - Générer un nouveau token JWT
        String newAccessToken = jwtService.generateToken(authentication);

        // Générer un nouveau refresh token
        String newRefreshToken = jwtService.generateRefreshToken(authentication);

        // Mettre à jour le refresh token en base de données
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(86400)); // 24 heures
        refreshTokenRepository.save(refreshToken);

        // Créer la réponse contenant le nouveau token JWT et le refresh token
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(newAccessToken);
        authResponseDto.setRefreshToken(newRefreshToken); // Si vous générez un nouveau refresh token

        return authResponseDto;
    }
}
