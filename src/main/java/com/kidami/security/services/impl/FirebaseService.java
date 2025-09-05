package com.kidami.security.services.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Service
public class FirebaseService {

    private  static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);
    @PostConstruct
    public void initialize() {
        try {
            logger.debug("Firebase initialize  tentative");
            // Vérifier si Firebase est déjà initialisé
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource("firebase-service-account-key.json");
                FileInputStream serviceAccount = new FileInputStream(resource.getFile());
                logger.debug("Initializing Firebase service avec configuration de fichier {}",resource);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.debug("Firebase initialized successfully!");
            } else {
                logger.debug("Firebase already initialized");
            }
        } catch (Exception e) {
            logger.error("Firebase initialization failed: {}", e.getMessage());
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public String createCustomToken(String uid) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }
}
