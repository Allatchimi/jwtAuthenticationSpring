package com.kidami.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {
    private String sessionId;
    private String userAgent;
    private String ipAddress;
    private Instant createdAt;
    private Instant expiryDate;
    private boolean currentSession;
}