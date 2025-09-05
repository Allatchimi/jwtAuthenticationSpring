package com.kidami.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

// Testez le mot de passe manuellement
@SpringBootTest
public class PasswordTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testPassword() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Encoded: " + encodedPassword);
        System.out.println("Matches: " + passwordEncoder.matches(rawPassword, encodedPassword));
    }
}