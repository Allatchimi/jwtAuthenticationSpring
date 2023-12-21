package com.kidami.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    //private final JwtAuthenticationFilter jwtAuthFilter;
    //
    //
    // private final AuthenticationProvider authenticationProvider;

//    private static final String[] AUTH_WHITELIST = {
//            // General
//            "/",
//            // Auth
//            "/api/v1/auth/**",
//            // Swagger UI
//            "/api-docs",
//            "/api-docs/**",
//            "/swagger-ui.html",
//            "/swagger-ui/**",
//            "/register"
//    };

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        http.cors(AbstractHttpConfigurer::disable);
//        http.csrf(AbstractHttpConfigurer::disable);
//        http.authorizeHttpRequests(request -> {
//            request.requestMatchers("/register").permitAll();
//            request.anyRequest().authenticated();
//        });
//        return http.build();

       return http.csrf().disable()
               .authorizeHttpRequests()
               .requestMatchers("/register","/v3/api-docs/**","/swagger-ui/**").permitAll()
               .and()
               .authorizeHttpRequests().requestMatchers("/api/**")
               .authenticated().and()
               .sessionManagement()
               .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               .and().build();

    }
}
