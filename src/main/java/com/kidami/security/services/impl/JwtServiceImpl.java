package com.kidami.security.services.impl;

import com.kidami.security.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtServiceImpl implements JwtService {
    private static final String SECRET_KEY = "AFCAF4422DF2188A2828D1A3B7C97";
    @Override
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
               // .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /*
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();

    }*/

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
        return Jwts.parser().setSigningKey(getSiginKey()).build().parseClaimsJws(token).getBody();
    }
    private Key getSiginKey(){
        byte[] key = Decoders.BASE64.decode("2879c5b4a1a8bfe972529ee8b3358b9fb23c1ac31528c3a05a2246067356e43e");
        return Keys.hmacShaKeyFor(key);
    }
}
