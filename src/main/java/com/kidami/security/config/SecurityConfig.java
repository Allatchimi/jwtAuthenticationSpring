package com.kidami.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF if working with REST APIs
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/auth/register", "/auth/login").permitAll(); // Allow access to registration and login pages
                    auth.anyRequest().authenticated(); // All other requests require authentication
                })
                .formLogin(form -> form
                        .loginPage("/auth/login").permitAll() // Specify the custom login page
                        .defaultSuccessUrl("/home", true) // Redirect to /home after successful login
                        .failureUrl("/auth/login?error=true") // Redirect to login page with error if login fails
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true").permitAll() // Redirect to login page after logout
                )
                .oauth2Login(form -> form
                        .loginPage("/auth/login") // Use the same login page for OAuth2 login
                        .defaultSuccessUrl("/home", true) // Redirect to /home after successful login via OAuth2
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
