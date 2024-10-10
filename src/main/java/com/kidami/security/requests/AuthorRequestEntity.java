package com.kidami.security.requests;

import lombok.Data;

@Data
public class AuthorRequestEntity {
    private String token;

    public AuthorRequestEntity() {}

    public AuthorRequestEntity(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}