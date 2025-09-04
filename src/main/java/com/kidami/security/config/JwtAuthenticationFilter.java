package com.kidami.security.config;

import com.kidami.security.services.JwtService;
import com.kidami.security.services.impl.CustomUserDetailsService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // Ne pas filtrer les endpoints d'authentification et les endpoints publics
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        // Vérifier la présence du header Authorization
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("JWT Token is missing for URI: {}", requestURI);
            chain.doFilter(request, response); // Laisser SecurityConfig gérer l'erreur
            return;
        }

        try {
            final String jwt = authorizationHeader.substring(7);
            final String email = jwtService.extractEmail(jwt);

            if (StringUtils.isEmpty(email)) {
                logger.warn("JWT Token does not contain email");
                chain.doFilter(request, response);
                return;
            }

            // Vérifier si l'utilisateur n'est pas déjà authentifié
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authenticated user: {}", email);
                } else {
                    logger.warn("JWT Token is invalid for user: {}", email);
                }
            }

        } catch (Exception e) {
            logger.error("JWT Authentication failed: {}", e.getMessage());
            // Ne pas throw d'exception, laisser le SecurityConfig gérer via Http403ForbiddenEntryPoint
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Exclure les endpoints qui ne nécessitent pas d'authentification JWT
        return path.startsWith("/api/auth/") ||
                path.startsWith("/login") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/swagger-resources/") ||
                path.startsWith("/webjars/") ||
                path.equals("/") ||
                path.startsWith("/register");
    }
}