package com.kidami.security.controllers;


import com.kidami.security.dto.AuthResponseDto;
import com.kidami.security.dto.LoginDTO;
import com.kidami.security.models.User;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.JwtService;
import com.kidami.security.services.UserService;
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

import java.util.Optional;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @GetMapping("/register")
    public String showRegistrationForm() {
        logger.info("Accessing registration form");
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String email, @RequestParam String password) {
        try {
            logger.info("Registering user with email: {}", email);
            userService.registerNewUser(email, password);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO,Model model) {
        try {
            // Authentifier l'utilisateur
               authService.login(loginDTO);

            // Ajouter le token au modèle ou gérer la redirection comme nécessaire
            return "redirect:/home";
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginDTO.getEmail());
            model.addAttribute("loginError", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/protected-endpoint")
    @ResponseBody // Indique que la réponse est directement le corps
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String email = authentication.getName(); // Le nom de l'utilisateur est l'email
        // Récupérer les informations de l'utilisateur à partir du UserService
        User user = userService.findByEmail(email); // Assurez-vous d'avoir une méthode pour récupérer l'utilisateur
        if (user != null) {
            return ResponseEntity.ok(user); // Retourner les informations de l'utilisateur
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
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
