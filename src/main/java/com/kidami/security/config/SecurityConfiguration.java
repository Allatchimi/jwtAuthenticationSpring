package com.kidami.security.config;

import com.kidami.security.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Autowired
    private JwtRequestFilter requestFilter;

    private static final String[] AUTH_WHITELIST = {
            // General
            "/",
            // Auth
            "/api/v1/auth/**",
            // Swagger UI
            "/api-docs",
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/register"
    };

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

       return http
               .csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests((authorize) -> authorize
                       .requestMatchers("/register","/v3/api-docs/**","/authentication","/swagger-ui/**").permitAll()
                       .requestMatchers("/api/**")
                       .authenticated()
                       .anyRequest().authenticated()
               )
               .sessionManagement((sessionManagement)->
                       sessionManagement
                               .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

               )
               .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
               .build();

    }
    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
