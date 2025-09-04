package com.kidami.security.controllers;

import com.kidami.security.dto.SessionInfo;
import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.User;
import com.kidami.security.repository.RefreshTokenRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final RefreshTokenRepository refreshTokenRepository;

    public SessionController(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping
    public List<SessionInfo> getUserSessions(@AuthenticationPrincipal User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);

        return tokens.stream()
                .map(token -> {
                    SessionInfo sessionInfo = new SessionInfo();
                    sessionInfo.setSessionId(token.getSessionId());
                    sessionInfo.setUserAgent(token.getUserAgent());
                    sessionInfo.setIpAddress(token.getIpAddress());
                    sessionInfo.setCreatedAt(token.getCreatedAt());
                    sessionInfo.setExpiryDate(token.getExpiryDate());
                    return sessionInfo;
                })
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{sessionId}")
    public void revokeSession(@AuthenticationPrincipal User user,
                              @PathVariable String sessionId) {
        refreshTokenRepository.deleteByUserAndSessionId(user, sessionId);
    }

    @DeleteMapping
    public void revokeAllSessions(@AuthenticationPrincipal User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}