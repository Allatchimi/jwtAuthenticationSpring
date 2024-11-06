package com.kidami.security.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {
    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // Replace with actual roles if needed
    }

    @Override
    public String getPassword() {
        return null; // Not applicable for OAuth2 users
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Using email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize according to your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize according to your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize according to your logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize according to your logic
    }

    public User getUser() {
        return user;
    }
}
