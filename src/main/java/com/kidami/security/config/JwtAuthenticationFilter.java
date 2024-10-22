package com.kidami.security.config;

import com.kidami.security.services.JwtService;
import com.kidami.security.services.impl.CustomUserDetailsService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Exclure certaines routes comme /login et /register
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/login") || requestURI.equals("/register")) {
            chain.doFilter(request, response);
            return; // ne pas appliquer le filtre pour ces routes
        }

        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        // Vérifier si le header contient un token JWT valide
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response); // continuer sans traitement JWT si pas de token
            return;
        }

        jwt = authorizationHeader.substring(7); // Extraire le token
        email = jwtService.extractEmail(jwt); // Extraire l'email à partir du token

        // S'assurer qu'il n'y a pas déjà une authentification pour cette session
        if (StringUtils.isNotEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Charger les détails de l'utilisateur depuis la base de données
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // Vérifier si le token est valide
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Créer un objet d'authentification et le définir dans le contexte de sécurité
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }
}
