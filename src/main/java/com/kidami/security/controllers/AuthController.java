package com.kidami.security.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.kidami.security.dto.authDTO.*;
import com.kidami.security.models.*;
import com.kidami.security.repository.RefreshTokenRepository;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.JwtService;
import com.kidami.security.services.UserService;
import com.kidami.security.services.impl.FirebaseService;
import com.kidami.security.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FirebaseService firebaseService;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService, JwtService jwtService, UserService userService, RefreshTokenRepository refreshTokenRepository, FirebaseService firebaseService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO registerDTO, BindingResult bindingResult,HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            // Les méthodes @AssertTrue sont exécutées automatiquement !
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            logger.info("Registering user with registerDTO: {}", registerDTO.getName());
            AuthResponseDto newUserDTO = authService.registerAndAuthenticate(registerDTO, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.created("User registered successfully", newUserDTO, null));
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.error("Error registering user", null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            AuthResponseDto authResponseDto = authService.login(loginDTO,request);
            // logger.info("User authenticated successfully: {}", loginDTO.getEmail());
            return ResponseEntity.ok(ResponseUtil.success("authenticated with succes",authResponseDto,HttpStatus.ACCEPTED));
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error("Authentication failed for user",loginDTO.getEmail(), e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        AuthResponseDto authResponseDto =  authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success("refresh token successfully",authResponseDto,null));
    }

    @GetMapping("/protected-endpoint")
    public ResponseEntity<ApiResponse<?>> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(ResponseUtil.success("les informations de l'utilisateur Récupérer",user,null)); // Retourner les informations de l'utilisateur
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error("user not found",null,null));
    }


    @PostMapping("/firebase")
    @Operation(summary = "Login with Firebase")
    public ResponseEntity<ApiResponse<?>> firebaseLogin(
            @RequestBody FirebaseLoginRequest firebaseLoginRequest,
            HttpServletRequest request) {


        try {

            logger.info("Firebase login attempt for token: {}",
                    safeTokenLog(firebaseLoginRequest.getIdToken()) + "...");

            AuthResponseDto authResponse = authService.firebaseLogin(
                    firebaseLoginRequest.getIdToken(),
                    request
            );

            return ResponseEntity.ok(ResponseUtil.success(
                    "Logged with Firebase successfully",
                    authResponse,
                    null
            ));

        } catch (AuthenticationServiceException e) {
            logger.error("Firebase authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseUtil.error(
                            "Invalid Firebase token",
                            null,
                            e.getMessage()
                    ));

        } catch (Exception e) {
            logger.error("Unexpected error during Firebase login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.error(
                            "Internal server error during Firebase login",
                            null,
                            e.getMessage()
                    ));
        }
    }

    // Méthode safe pour logger les tokens
    private String safeTokenLog(String token) {
        if (token == null) return "null";
        if (token.length() <= 10) return token;
        return token.substring(0, 10) + "...";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        logger.info("Accessing home page");

        // Récupérer l'utilisateur connecté
        String email = authentication.getName(); // Le nom de l'utilisateur est l'email

        // Récupérer les informations de l'utilisateur à partir du UserService
        User user = userService.findByEmail(email); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur

        // Ajouter l'utilisateur au modèle pour qu'il soit accessible dans le template
        model.addAttribute("user", user);

        return "home"; // Retourne le nom de la vue Thymeleaf
    }


}
