package com.kidami.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.example.com")  // URL de base de l'API si nécessaire
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
