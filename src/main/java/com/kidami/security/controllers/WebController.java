package com.kidami.security.controllers;


import com.kidami.security.dto.authDTO.RegisterDTO;
import com.kidami.security.dto.authDTO.LoginDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.models.User;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.AuthService;
import com.kidami.security.services.UserService;
import com.kidami.security.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    private final AuthService authService;
    private final UserService userService;

    public WebController(AuthService authService,UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @ModelAttribute RegisterDTO registerDTO) {
        try {
           // logger.info("Registering user with email: {}", registerDTO);
           UserDTO userDTO =  userService.registerNewUser(registerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success("registered succefully",userDTO,null));

        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error("Error registering user",null,e.getMessage()));
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO,Model model, HttpServletRequest request) {
        try {
               authService.login(loginDTO, request);
            return "redirect:/home";
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginDTO.getEmail());
            model.addAttribute("loginError", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/protected-endpoint")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        logger.info("Accessing home page");
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "home";
    }


}
