package com.kidami.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.kidami.security.services.StorageService;
import com.kidami.security.services.impl.FirebaseService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FirebaseAuthTest {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthTest.class);
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FirebaseService firebaseService;


    @Test
    void testRealFirebaseLogin() throws Exception {
        logger.info("🔵 Début du test avec VRAI token Firebase");

        // Remplacez par un VRAI token Firebase obtenu comme ci-dessus
        String realFirebaseToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVmMjQ4ZjQyZjc0YWUwZjk0OTIwYWY5YTlhMDEzMTdlZjJkMzVmZTEiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoibGFtaW5lIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3VsZWFybmluZy1hcHAtYjQ4OGUiLCJhdWQiOiJ1bGVhcm5pbmctYXBwLWI0ODhlIiwiYXV0aF90aW1lIjoxNzU3MDA3MzIwLCJ1c2VyX2lkIjoiNzhiZk55QTJwMVlzNzMzdjc2ZXJDNDlrcjJlMiIsInN1YiI6Ijc4YmZOeUEycDFZczczM3Y3NmVyQzQ5a3IyZTIiLCJpYXQiOjE3NTcwMDczMjAsImV4cCI6MTc1NzAxMDkyMCwiZW1haWwiOiJrZWxsYW5hbWluZUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJrZWxsYW5hbWluZUBnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.THk7utTstDiDTfWiMMtafQYZ8iQ9UD-JUJzOkPnEuOcQoMgHiCifhMiB9dBPZEYWnMG7ZOGP-W9ioVo7QsXv9K0fb7fZoxLkp0a2iOMB1Mhl3RekbFOstQQm9uugiHOdirGn4n94wBx-zFGqit51stTEgpg42ijkKST9QLtxpYI3opoS5NFu3HX810W23dg0N5giAGTluw4Ve2sVR2oOMUu6ftdIFVY7XBQ3VfGzgzgEkuyX1-xB-5d6lGuZKpztLrpUCsLhYqqi-e9uPCe0jdQZyzUc89iJyOlYICyPXm5ekbtCokKpZ2e83HguTPsrPxj0PJxpNqT98dVMv62Oog"; // Token Firebase réel

        // D'abord, vérifiez que Firebase est bien initialisé
        try {
            // Cette ligne peut échouer si Firebase n'est pas configuré
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(realFirebaseToken);
            logger.info("✅ Token Firebase valide pour: {}", decoded.getEmail());
        } catch (Exception e) {
            logger.error("❌ Token Firebase invalide ou configuration manquante: {}", e.getMessage());
            assumeTrue(false, "Firebase non configuré, test ignoré");
        }

        mockMvc.perform(post("/api/auth/firebase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idToken\":\"" + realFirebaseToken + "\"}"))
                .andDo(result -> {
                    logger.info("Status: {}", result.getResponse().getStatus());
                    logger.info("Response: {}", result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.user.email").exists());

        logger.info("✅ Test réussi avec vrai token Firebase");
    }

    @Test
    void testFirebaseLoginWithValidToken() throws Exception {

        logger.info("🔵 Début du test Firebase avec token testFirebaseLoginWithValidToken");
        // Mock Firebase token verification
        FirebaseToken mockToken = mock(FirebaseToken.class);
        when(mockToken.getEmail()).thenReturn("test@example.com");
        when(mockToken.getUid()).thenReturn("test-uid");
        when(mockToken.getName()).thenReturn("Test User");

        when(firebaseService.verifyToken(anyString())).thenReturn(mockToken);

        mockMvc.perform(post("/api/auth/firebase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idToken\":\"valid-token\"}"))
                .andDo(result -> {
                            // Log personnalisé
                            System.out.println("=== REQUEST ===");
                            System.out.println("URL: " + result.getRequest().getRequestURI());
                            System.out.println("Body: " + result.getRequest().getContentAsString());

                            System.out.println("=== RESPONSE ===");
                            System.out.println("Status: " + result.getResponse().getStatus());
                            System.out.println("Body: " + result.getResponse().getContentAsString());
                        }
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        logger.info("✅ Test réussi");

        verify(firebaseService, times(1)).verifyToken("valid-token");
    }

    @Test
    void testFirebaseLoginWithInvalidToken() throws Exception {
        // Mock Firebase exception
        when(firebaseService.verifyToken(anyString()))
                .thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/auth/firebase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idToken\":\"invalid-token\"}"))
                .andExpect(status().isUnauthorized());
    }
}