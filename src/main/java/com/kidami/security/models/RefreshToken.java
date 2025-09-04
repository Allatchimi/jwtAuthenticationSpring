package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne // ← CHANGER de @OneToOne à @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant expiryDate;
    private String sessionId; // ID unique pour chaque session
    private String userAgent; // Navigateur/device info
    private String ipAddress; // Adresse IP
    private Instant createdAt;

    // Constructor
    public RefreshToken() {
        this.createdAt = Instant.now();
        this.sessionId = UUID.randomUUID().toString();
    }

}