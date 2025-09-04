package com.kidami.security.dto;

import java.time.Instant;

public class SessionInfo {
    private String sessionId;
    private String userAgent;
    private String ipAddress;
    private Instant createdAt;
    private Instant expiryDate;
    private boolean currentSession;

    // Constructeurs
    public SessionInfo() {}

    public SessionInfo(String sessionId, String userAgent, String ipAddress,
                       Instant createdAt, Instant expiryDate) {
        this.sessionId = sessionId;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
        this.expiryDate = expiryDate;
    }

    // Getters et Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }

    public boolean isCurrentSession() { return currentSession; }
    public void setCurrentSession(boolean currentSession) { this.currentSession = currentSession; }
}