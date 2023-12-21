package com.kidami.security.controllers;

import com.kidami.security.dto.SignUpRequest;
import com.kidami.security.dto.UserDto;
import com.kidami.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpUserController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody SignUpRequest signUpRequest)  {
        UserDto createdUser = authService.createUser(signUpRequest);
        if (createdUser == null)
            return new ResponseEntity<>("user is not created try again later", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
