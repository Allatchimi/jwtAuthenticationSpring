package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Profile("prod")
public class SupabaseStorageService implements StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public SupabaseStorageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(supabaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        try {
            String fileName = generateSecureFileName(file.getOriginalFilename());

            String respons = webClient.post()
                    .uri("/storage/v1/object/{bucket}/{fileName}", bucket, fileName)
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header("Cache-Control", "max-age=31536000")
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> Mono.error(new IOException("Erreur Supabase: " + response.statusCode())))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(45))
                    .block();

            return getFileUrl(fileName);

        } catch (Exception e) {
            throw new IOException("Erreur lors de l'upload vers Supabase", e);
        }
    }

    @Override
    public Resource loadFile(String fileName) throws IOException {
        try {
            String publicUrl = getFileUrl(fileName);
            return new UrlResource(URI.create(publicUrl));
        } catch (Exception e) {
            throw new IOException("Erreur lors du chargement du fichier depuis Supabase", e);
        }
    }

    @Override
    public List<String> listFiles() throws IOException {
        try {
            return webClient.get()
                    .uri("/storage/v1/object/list/{bucket}", bucket)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> Mono.error(new IOException("Erreur liste Supabase: " + response.statusCode())))
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
        } catch (Exception e) {
            throw new IOException("Erreur lors du listing des fichiers", e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        try {
            webClient.delete()
                    .uri("/storage/v1/object/{bucket}/{fileName}", bucket, fileName)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> Mono.error(new IOException("Erreur suppression Supabase: " + response.statusCode())))
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(30))
                    .block();

            return true;
        } catch (Exception e) {
            throw new IOException("Erreur lors de la suppression du fichier", e);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseUrl.trim(), bucket.trim(), fileName);
    }

    private String generateSecureFileName(String originalFileName) {
        String cleanName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_") : "file";

        return String.format("%s_%d_%s",
                UUID.randomUUID().toString().substring(0, 8),
                System.currentTimeMillis(),
                cleanName);
    }
}