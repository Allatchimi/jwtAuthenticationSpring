package com.kidami.security.dto;

import com.kidami.security.models.Role;

import java.util.Set;

public class AddRoleRequest {
    private String email;
    private Set<String> roles; // Using strings to represent role names

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
