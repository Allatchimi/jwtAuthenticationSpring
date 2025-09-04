package com.kidami.security.services.impl;

import com.kidami.security.models.*;
import com.kidami.security.repository.RefreshTokenRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
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
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public CustomOAuth2UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            return processOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationException ex) {
            logger.warn("OAuth2 authentication exception", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error processing OAuth2 user", ex);
            OAuth2AuthenticationException authException = new OAuth2AuthenticationException(
                    "Authentication processing failed: " + ex.getMessage()
            );
            authException.initCause(ex);
            throw authException;
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String providerName = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        AuthProvider provider = AuthProvider.valueOf(providerName);
        String providerId = extractProviderId(oAuth2User, provider);

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider");
        }
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(provider, providerId);
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByEmail(email);
        }
        User user = userOptional.map(existingUser -> {
            if (!existingUser.getProvider().equals(provider)) {
                throw new OAuth2AuthenticationException("Vous vous êtes inscrit avec " + existingUser.getProvider() +
                        ". Veuillez utiliser votre compte " + existingUser.getProvider() + " pour vous connecter.");
            }
            return updateExistingUser(existingUser, oAuth2User);
        }).orElseGet(() -> registerNewUser(provider, providerId, oAuth2User));

        saveOrUpdateRefreshToken(user);

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }


    private String extractProviderId(OAuth2User oAuth2User, AuthProvider provider) {
        switch (provider) {
            case GITHUB:
            case FACEBOOK:
                Object idAttribute = oAuth2User.getAttribute("id");
                return idAttribute != null ? idAttribute.toString() : null;
            case GOOGLE:
            default:
                Object subAttribute = oAuth2User.getAttribute("sub");
                return subAttribute != null ? subAttribute.toString() : null;
        }
    }

    private User registerNewUser(AuthProvider provider, String providerId, OAuth2User oAuth2User) {
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setFirstName(extractFirstName(oAuth2User));
        user.setLastName(extractLastName(oAuth2User));
        user.setName(extractFullName(oAuth2User));
        user.setProfileImageUrl(oAuth2User.getAttribute("picture"));
        user.setEmailVerified(true);

        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.USER);
        user.setRoles(defaultRoles);

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2User oAuth2User) {
        existingUser.setFirstName(extractFirstName(oAuth2User));
        existingUser.setLastName(extractLastName(oAuth2User));
        existingUser.setName(extractFullName(oAuth2User));
        existingUser.setProfileImageUrl(oAuth2User.getAttribute("picture"));
        return userRepository.save(existingUser);
    }

    private String getStringAttribute(OAuth2User oAuth2User, String attributeName) {
        Object attribute = oAuth2User.getAttribute(attributeName);
        return attribute != null ? attribute.toString() : null;
    }

    private String getStringAttribute(OAuth2User oAuth2User, String... attributeNames) {
        for (String attributeName : attributeNames) {
            Object attribute = oAuth2User.getAttribute(attributeName);
            if (attribute != null) {
                return attribute.toString();
            }
        }
        return null;
    }

    private String extractFirstName(OAuth2User oAuth2User) {
        return getStringAttribute(oAuth2User, "given_name", "first_name", "firstname");
    }

    private String extractLastName(OAuth2User oAuth2User) {
        return getStringAttribute(oAuth2User, "family_name", "last_name", "lastname");
    }

    private String extractFullName(OAuth2User oAuth2User) {
        String name = getStringAttribute(oAuth2User, "name");
        if (name != null) {
            return name;
        }

        // Construire le nom complet à partir des parties
        String firstName = extractFirstName(oAuth2User);
        String lastName = extractLastName(oAuth2User);

        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }

        return null;
    }

    private void saveOrUpdateRefreshToken(User user) {
        // Générer un VRAI refresh token JWT
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        // Utiliser votre JwtService pour générer un vrai refresh token
        String refreshTokenValue = jwtService.generateRefreshToken(authentication);

        // Ensuite sauvegarder ce vrai refresh token
        List<RefreshToken> userTokens = refreshTokenRepository.findByUser(user);
        RefreshToken refreshToken = userTokens.isEmpty() ? new RefreshToken() : userTokens.get(0);

        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(86400));

        refreshTokenRepository.save(refreshToken);
    }
}