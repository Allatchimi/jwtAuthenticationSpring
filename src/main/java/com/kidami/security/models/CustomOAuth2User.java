package com.kidami.security.models;

import com.kidami.security.enums.AuthProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {
    private final User user;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(User user, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    // Constructeur alternatif sans authorities (les récupère depuis l'user)
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
        this.authorities = user.getAuthorities(); // Utilise les authorities de l'user
    }

    @Override
    public String getName() {
        if (user.getName() != null && !user.getName().isEmpty()) {
            return user.getName();
        } else if (user.getFirstName() != null && user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        } else {
            return user.getEmail();
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Peut être null pour les utilisateurs OAuth2
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public User getUser() {
        return user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    public AuthProvider getProvider() {
        return user.getProvider();
    }

    public String getProviderId() {
        return user.getProviderId();
    }

    // Méthode utilitaire pour vérifier si c'est un utilisateur OAuth2
    public boolean isOAuth2User() {
        return user.getProvider() != AuthProvider.LOCAL;
    }

    @Override
    public String toString() {
        return "CustomOAuth2User{" +
                "user=" + user +
                ", email='" + getEmail() + '\'' +
                ", provider=" + getProvider() +
                '}';
    }
}