package com.kidami.security.services.impl;

import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class JwtServiceImpl implements JwtService {
    private static final String SECRET_KEY = "AFCAF4422DF2188A2828D1A3B7C97AFCAF4422DF2188A2828D1A3B7C97AFCAF4422DF2188A2828D1A3B7C97";
    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final UserRepository userRepository;

    public JwtServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // new Date(System.currentTimeMillis())
    @Override
    public String generateToken(Authentication authentication) {
        String email;
        String name;
        Collection<? extends GrantedAuthority> authorities;
        Long userId = null;

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {
            email = oauthUser.getAttribute("email");
            name = oauthUser.getAttribute("name");
            authorities = authentication.getAuthorities();
            // Pour OAuth2, vous devrez peut-être chercher l'user en base
            Optional<User> userOpt = userRepository.findByEmail(email);
            userId = userOpt.map(User::getId).orElse(null);
        } else if (authentication.getPrincipal() instanceof User user) {
            email = user.getEmail();
            name = user.getName();
            authorities = user.getAuthorities();
            userId = user.getId();
        } else {
            email = authentication.getName();
            name = email;
            authorities = authentication.getAuthorities();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("name", name);
        if (userId != null) {
            claims.put("userId", userId);
        }
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        String email = authentication.getName();

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Expiration après 7 jours
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, String userEmail) {
        final  String email =  extractEmail(token);
        return (email.equals(userEmail) && !isTokenExpired(token));
    }

    private Date extractExpiration(String token) {
       return  extractClaim(token, Claims::getExpiration);
    }
    /*
    public boolean isTokenValid(String token, UserDetails userDetails){
        final  String username = extractUserName(token);
        return  (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }*/
    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers){
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token){
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        return Jwts.parser().setSigningKey(getSiginKey()).build().parseClaimsJws(token).getBody();
    }


    private Key getSiginKey(){
        byte[] key = Decoders.BASE64.decode("2879c5b4a1a8bfe972529ee8b3358b9fb23c1ac31528c3a05a2246067356e43e");
        return Keys.hmacShaKeyFor(key);
    }

}
