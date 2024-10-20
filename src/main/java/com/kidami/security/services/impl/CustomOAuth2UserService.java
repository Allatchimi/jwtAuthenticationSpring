package com.kidami.security.services.impl;

import com.kidami.security.models.CustomOAuth2User;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LogManager.getLogger(CustomOAuth2UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // Log the retrieved attributes
        logger.info("Retrieved OAuth2 user attributes: {}", oAuth2User);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Log specific attributes
        logger.info("User email: {}", email);
        logger.info("User name: {}", name);
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider("GOOGLE");
            return userRepository.save(newUser);
        });
        return oAuth2User;
    }
}

