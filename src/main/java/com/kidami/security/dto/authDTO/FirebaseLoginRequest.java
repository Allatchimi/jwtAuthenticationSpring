package com.kidami.security.dto.authDTO;

import lombok.Data;

@Data
public class FirebaseLoginRequest {
    private String idToken;

    // Getters and setters
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}