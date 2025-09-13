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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {


    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final FirebaseService firebaseService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           RefreshTokenRepository refreshTokenRepository,
                           UserRepository userRepository,
                           JwtService jwtService,
                           FirebaseService firebaseService,
                           UserService userService,
                           UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
    public AuthResponseDto firebaseLogin(String idToken, HttpServletRequest request) {
        logger.debug("Starting Firebase login process for token: {}",
                safeTokenLog(idToken) + "...");

        try {
            // 1. Vérifier le token Firebase
            FirebaseToken decodedToken = firebaseService.verifyToken(idToken);
            logger.debug("Firebase token verified for email: {}", decodedToken.getEmail());

            String email = decodedToken.getEmail();
            String uid = decodedToken.getUid();

            // 2. Trouver ou créer l'utilisateur
            User user = findOrCreateUserFromFirebase(decodedToken, uid, email);
            logger.debug("User processed: {}", user.getEmail());

            // 3. Générer les tokens JWT
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());

            String token = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            Instant expiryDate = Instant.now().plus(Duration.ofDays(30));

            // 4. Gérer le refresh token
            createOrUpdateRefreshToken(user, refreshToken, expiryDate, request);

            // 5. Préparer la réponse
            UserResponseDTO userResponse = convertUserToResponseDTO(user);

            return AuthResponseDto.fromTokens(
                    token,
                    refreshToken,
                    userResponse,
                    expiryDate
            );

        } catch (FirebaseAuthException e) {
            logger.error("Firebase authentication failed for token: {}",
                    safeTokenLog(idToken) + "...", e);
            throw new AuthenticationServiceException("Firebase authentication failed", e);
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase login for token: {}",
                    safeTokenLog(idToken) + "...", e);
            throw new AuthenticationServiceException("Login process failed", e);
        }
    }

    private User findOrCreateUserFromFirebase(FirebaseToken decodedToken, String uid, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Vérifier et mettre à jour le provider si nécessaire
            if (!existingUser.getProvider().equals(AuthProvider.FIREBASE)) {
                logger.info("Updating user {} provider from {} to FIREBASE",
                        email, existingUser.getProvider());
                existingUser.setProvider(AuthProvider.FIREBASE);
                existingUser.setProviderId(uid);
                return userRepository.save(existingUser);
            }

            // Mettre à jour les informations même si le provider est déjà FIREBASE
            existingUser.setName(decodedToken.getName());
            extractAndSetNamesFromFirebaseToken(decodedToken, existingUser);
            return userRepository.save(existingUser);

        } else {
            // Créer un nouvel utilisateur
            logger.info("Creating new Firebase user: {}", email);
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setProvider(AuthProvider.FIREBASE);
            newUser.setProviderId(uid);
            newUser.setName(decodedToken.getName());
            newUser.setEmailVerified(true);

            extractAndSetNamesFromFirebaseToken(decodedToken, newUser);

            Set<Role> defaultRoles = new HashSet<>();
            defaultRoles.add(Role.STUDENT);
            newUser.setRoles(defaultRoles);



            return userRepository.save(newUser);
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

    // Méthode safe pour logger les tokens
    private String safeTokenLog(String token) {
        if (token == null) return "null";
        if (token.length() <= 10) return token;
        return token.substring(0, 10) + "...";
    }
}
