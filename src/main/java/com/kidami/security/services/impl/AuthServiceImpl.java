package com.kidami.security.services.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.kidami.security.dto.authDTO.AuthResponseDto;
import com.kidami.security.dto.authDTO.LoginDTO;
import com.kidami.security.dto.authDTO.RefreshTokenRequest;
import com.kidami.security.dto.authDTO.RegisterDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.dto.authDTO.UserResponseDTO;
import com.kidami.security.mappers.UserMapper;
import com.kidami.security.models.AuthProvider;
import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;
import com.kidami.security.repository.RefreshTokenRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.JwtService;
import com.kidami.security.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseService firebaseService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           RefreshTokenRepository refreshTokenRepository,
                           UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           FirebaseService firebaseService,
                           UserService userService,
                           UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.firebaseService = firebaseService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponseDto login(LoginDTO loginDTO, HttpServletRequest request) {
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
        createOrUpdateRefreshToken(user, refreshToken, expiryDate, request);

        UserResponseDTO userResponse = userMapper.userToUserResponseDTO(user);

        return AuthResponseDto.fromTokens(
                token,
                refreshToken,
                userResponse,
                expiryDate

        );
    }

    @Override
    public AuthResponseDto registerAndAuthenticate(RegisterDTO registerDTO, HttpServletRequest request) {
        try {
            // 1. Créer l'utilisateur via UserService
            UserDTO userDTO = userService.registerNewUser(registerDTO);

            // 2. Charger l'user COMPLET depuis la base avec les rôles
            User user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. Créer Authentication avec les VRAIS authorities
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities() // ← CORRECTION ICI
            );
            // 4. Générer les tokens
            String token = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            Instant expiryDate = Instant.now().plus(Duration.ofDays(30));

            createOrUpdateRefreshToken(user, refreshToken, expiryDate, request);
            // 5. Conversion MANUELLE pour tester
            UserResponseDTO userResponse = userMapper.userToUserResponseDTO(user);

            return AuthResponseDto.fromTokens(
                    token,
                    refreshToken,
                    userResponse,
                    Instant.now().plus(Duration.ofDays(30)));

        } catch (Exception e) {
            System.out.println("Error in registerAndAuthenticate: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public AuthResponseDto firebaseLogin(String idToken,HttpServletRequest request) {
        try {
            // Vérifier le token Firebase
            FirebaseToken decodedToken = firebaseService.verifyToken(idToken);
            String email = decodedToken.getEmail();
            String uid = decodedToken.getUid();

            // Vérifier si l'utilisateur existe
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
                // Mettre à jour le provider si nécessaire
                if (!user.getProvider().equals(AuthProvider.FIREBASE)) {
                    user.setProvider(AuthProvider.FIREBASE);
                    user.setProviderId(uid);
                    user = userRepository.save(user);
                }
            } else {
                // Créer un nouvel utilisateur
                user = new User();
                user.setEmail(email);
                user.setProvider(AuthProvider.FIREBASE);
                user.setProviderId(uid);
                // CORRECTION ICI : Utiliser getName() au lieu de getClaim()
                user.setName(decodedToken.getName());
                // Extraire le prénom et nom si possible
                extractAndSetNamesFromFirebaseToken(decodedToken, user);
                // Set default roles
                Set<Role> defaultRoles = new HashSet<>();
                defaultRoles.add(Role.USER);
                user.setRoles(defaultRoles);
                user = userRepository.save(user);
            }
            // Générer les tokens JWT
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());

            String token = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            Instant expiryDate = Instant.now().plus(Duration.ofDays(30));

            createOrUpdateRefreshToken(user, refreshToken, expiryDate, request);
            // Convertir User en UserResponseDTO pour la réponse
            UserResponseDTO userResponse = convertUserToResponseDTO(user);

            AuthResponseDto authResponseDto = new AuthResponseDto();
            authResponseDto.setAccessToken(token);
            authResponseDto.setRefreshToken(refreshToken);
            authResponseDto.setUser(userResponse);

            return authResponseDto;

        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase authentication failed", e);
        }
    }
    // Méthode helper pour extraire les noms du token Firebase
    private void extractAndSetNamesFromFirebaseToken(FirebaseToken decodedToken, User user) {
        // Utiliser getName() qui retourne le nom complet
        String fullName = decodedToken.getName();
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setName(fullName);

            // Essayer de parser le nom complet en prénom/nom
            String[] nameParts = fullName.split(" ", 2);
            if (nameParts.length >= 1) {
                user.setFirstName(nameParts[0]);
            }
            if (nameParts.length >= 2) {
                user.setLastName(nameParts[1]);
            }
        }
        // Vous pouvez aussi utiliser les claims disponibles
        Map<String, Object> claims = decodedToken.getClaims();
        if (claims != null) {
            // Essayer différents champs possibles pour le prénom/nom
            if (claims.containsKey("given_name")) {
                user.setFirstName(claims.get("given_name").toString());
            }
            if (claims.containsKey("family_name")) {
                user.setLastName(claims.get("family_name").toString());
            }
            if (claims.containsKey("picture")) {
                user.setProfileImageUrl(claims.get("picture").toString());
            }
        }
    }
    // Méthode pour convertir User en UserResponseDTO
    private UserResponseDTO convertUserToResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setProvider(user.getProvider());
       // response.setProviderId(user.getProviderId());
        response.setEmailVerified(user.isEmailVerified());
        response.setRoles(user.getRoles());
        return response;
    }


    @Override
    public RefreshToken createOrUpdateRefreshToken(User user, String newToken, Instant newExpiryDate,
                                                   HttpServletRequest request) {

        // Extraire les infos de la requête
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String sessionId = (String) request.getSession().getId();

        // Chercher par session existante
        Optional<RefreshToken> existingToken = refreshTokenRepository
                .findByUserAndSessionId(user, sessionId);

        if (existingToken.isPresent()) {
            // Mettre à jour le token existant
            RefreshToken refreshToken = existingToken.get();
            refreshToken.setToken(newToken);
            refreshToken.setExpiryDate(newExpiryDate);
            return refreshTokenRepository.save(refreshToken);
        }

        // Créer un nouveau token pour une nouvelle session
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(newToken);
        refreshToken.setExpiryDate(newExpiryDate);
        refreshToken.setUser(user);
        refreshToken.setSessionId(sessionId);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);

        return refreshTokenRepository.save(refreshToken);
    }

    // Méthode utilitaire pour récupérer l'IP du client
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    @Override
    public void deleteRefreshTokenForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public void logout(User user) {

    }
    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            // 01 - Extraire l'email et vérifier le format du token
            if (refreshTokenRequest.getToken() == null || refreshTokenRequest.getToken().isEmpty()) {
                throw new RuntimeException("Refresh token is required");
            }

            String userEmail = jwtService.extractEmail(refreshTokenRequest.getToken());

            // 02 - Vérifier si l'utilisateur existe
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 03 - Chercher le refresh token par sa valeur
            RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.getToken())
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            // 04 - Vérifications de sécurité
            if (!refreshToken.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Token doesn't belong to user");
            }

            if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(refreshToken); // Nettoyer le token expiré
                throw new RuntimeException("Refresh token expired");
            }

            // 05 - Créer l'authentification
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );

            // 06 - Générer nouveaux tokens
            String newAccessToken = jwtService.generateToken(authentication);
            String newRefreshToken = jwtService.generateRefreshToken(authentication);

            // 07 - Mettre à jour le refresh token
            refreshToken.setToken(newRefreshToken);
            refreshToken.setExpiryDate(Instant.now().plusSeconds(86400)); // 24h
            refreshTokenRepository.save(refreshToken);

            UserResponseDTO userResponse = convertUserToResponseDTO(user);

            return AuthResponseDto.fromTokens(
                    newAccessToken,
                    newRefreshToken,
                    userResponse,
                    refreshToken.getExpiryDate()
            );

        } catch (Exception e) {
            throw new RuntimeException("Refresh token failed: " + e.getMessage(), e);
        }
    }
}
