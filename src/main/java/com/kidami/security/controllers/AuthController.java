package com.kidami.security.controllers;


import com.kidami.security.dto.*;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.JwtService;
import com.kidami.security.services.UserService;
import com.kidami.security.utils.ResponseUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        logger.info("Accessing registration form");
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            logger.info("Registering user with registerDTO: {}", registerDTO);
            UserDTO newUserDTO = userService.registerNewUser(registerDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtil.created("User registered successfully", newUserDTO, null));
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtil.error("Error registering user", null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO loginDTO) {
        try {
            //01 - Receive the token from AuthService
            AuthResponseDto authResponseDto = authService.login(loginDTO);

            // Log après authentification réussie
            // logger.info("User authenticated successfully: {}", loginDTO.getEmail());
            //03 - Return the response to the user
            return ResponseEntity.ok(ResponseUtil.success("authenticated with succes",authResponseDto,HttpStatus.OK));
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
        // Récupérer l'utilisateur connecté
        String email = authentication.getName(); // Le nom de l'utilisateur est l'email
        // Récupérer les informations de l'utilisateur à partir du UserService
        User user = userService.findByEmail(email); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur
        if (user != null) {
            return ResponseEntity.ok(ResponseUtil.success("les informations de l'utilisateur Récupérer",user,null)); // Retourner les informations de l'utilisateur
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error("user not found",null,null));
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
