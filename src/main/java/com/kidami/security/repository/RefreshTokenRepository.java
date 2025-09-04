package com.kidami.security.repository;

import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findByUser(User user); // Tous les tokens d'un user
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserAndSessionId(User user, String sessionId);
    void deleteByUserAndSessionId(User user, String sessionId);
    void deleteByUser(User user);
    void deleteByExpiryDateBefore(Instant expiryDate); // Nettoyage automatique
}