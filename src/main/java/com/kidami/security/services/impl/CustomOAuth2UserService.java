package com.kidami.security.services.impl;

import com.kidami.security.models.RefreshToken;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;
import com.kidami.security.repository.RefreshTokenRepository;
import com.kidami.security.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LogManager.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);


        //String email = oAuth2User.getAttribute("email");
      //  String name = oAuth2User.getAttribute("name");

        // Get OAuth2 access token
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        String accessTokenValue = accessToken.getTokenValue();

        // Save user and token in the database using RefreshToken entity
        saveOAuth2UserAndTokens(oAuth2User, accessTokenValue);

        return oAuth2User;
    }

    private void saveOAuth2UserAndTokens(OAuth2User oAuth2User, String accessToken) {
        String email = oAuth2User.getAttribute("email");
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;

        if (existingUser.isPresent()) {
            // If the user exists, use the existing user
            user = existingUser.get();
        } else {
            // Create a new user
            user = new User();
            user.setEmail(email);
            user.setName(oAuth2User.getAttribute("name"));
            user.setProvider("GOOGLE");
            //user.setRoles((Set<Role>) List.of(Role.USER));
            Set<Role> defaultRoles = new HashSet<>();
            defaultRoles.add(Role.USER); // Supposons que vous ayez un rôle USER dans l'énumération
            user.setRoles(defaultRoles);
            user = userRepository.save(user);  // Save the new user
        }

        // Check if there's already a refresh token entry for the user
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);

        if (existingRefreshToken.isPresent()) {
            // Update the existing refresh token with the new access token
            RefreshToken refreshToken = existingRefreshToken.get();
            refreshToken.setToken(accessToken);
            refreshToken.setExpiryDate(Instant.now().plusSeconds(86400));  // Set expiry to 24 hours
            refreshTokenRepository.save(refreshToken);
        } else {
            // Create a new refresh token entry
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(accessToken);
            refreshToken.setExpiryDate(Instant.now().plusSeconds(86400));  // Set expiry to 24 hours
            refreshTokenRepository.save(refreshToken);
        }
    }
}
